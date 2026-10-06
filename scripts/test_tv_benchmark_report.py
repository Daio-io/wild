#!/usr/bin/env python3
import json
import tempfile
import unittest
from pathlib import Path

from tv_benchmark_report import (
    build_comparison_summary,
    build_session_summary,
    extract_variant_metrics,
    percent_delta,
    resolve_variant_alias,
    session_compatibility_error,
    session_invalid_reason,
    write_session_artifacts,
)

FIXTURE = Path(__file__).parent / "testdata" / "aftr_sample_benchmarkData.json"


class TvBenchmarkReportTest(unittest.TestCase):
    def test_resolve_variant_alias_includes_lambda_aliases(self):
        self.assertEqual(
            resolve_variant_alias("lambda"),
            ("wild_lambda", "scrollGridWithWildLambda"),
        )
        self.assertEqual(
            resolve_variant_alias("lambda_recreated"),
            ("wild_lambda_recreated", "scrollGridWithWildLambdaRecreated"),
        )
        self.assertEqual(
            resolve_variant_alias("clickable"),
            ("wild_clickable", "scrollGridWithWildClickable"),
        )

    def test_extract_variant_metrics_reads_frame_and_memory(self):
        metrics = extract_variant_metrics(FIXTURE, benchmark_name="scrollGridWithWildClickable")
        self.assertEqual(metrics["frameCount"]["median"], 831.0)
        self.assertAlmostEqual(metrics["frameDurationCpuMs"]["P50"], 5.902167, places=5)
        self.assertEqual(metrics["memoryHeapSizeMaxKb"]["median"], 9288.0)
        self.assertGreater(metrics["totalRunTimeNs"], 0)
        self.assertEqual(
            metrics["frameOverrunMs"],
            {"available": False, "reason": "missing_metric"},
        )

    def test_percent_delta_handles_baseline(self):
        self.assertAlmostEqual(percent_delta(9.0, 12.0), -25.0)
        self.assertIsNone(percent_delta(1.0, 0.0))

    def test_summary_includes_deltas_and_human_verdict_note(self):
        session = {
            "profile": "local_short",
            "device": {"model": "AFTR", "androidVersion": "9"},
            "gitSha": "abc1234",
            "composeVersion": "1.11.1",
            "variants": ["wild_clickable", "wild_container", "material_surface"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "wild_clickable": extract_variant_metrics(
                            FIXTURE, "scrollGridWithWildClickable"
                        ),
                        "wild_container": {
                            "frameCount": {"median": 828.0},
                            "frameDurationCpuMs": {
                                "P50": 5.92,
                                "P90": 7.70,
                                "P95": 8.29,
                                "P99": 9.51,
                            },
                            "memoryHeapSizeMaxKb": {"median": 9815.0},
                            "totalRunTimeNs": 255439000000,
                        },
                        "material_surface": {
                            "frameCount": {"median": 512.0},
                            "frameDurationCpuMs": {
                                "P50": 6.50,
                                "P90": 9.04,
                                "P95": 10.25,
                                "P99": 12.65,
                            },
                            "memoryHeapSizeMaxKb": {"median": 10000.0},
                            "totalRunTimeNs": 268220000000,
                        },
                    },
                }
            ],
        }
        summary = build_session_summary(session)
        self.assertIn("AFTR", summary)
        self.assertNotIn("192.168.", summary)
        self.assertIn("material_surface", summary)
        self.assertIn("wild_container", summary)
        self.assertIn("human-reviewed", summary.lower())
        self.assertIn("P99", summary)
        self.assertIn("harness wall time", summary.lower())
        self.assertNotIn("Runtime (s)", summary)

    def test_write_session_artifacts_emits_json_and_markdown(self):
        session = {
            "profile": "confirmation",
            "device": {"model": "AFTR", "androidVersion": "9"},
            "gitSha": "deadbeef",
            "composeVersion": "1.11.1",
            "variants": ["material_surface"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "material_surface": {
                            "frameCount": {"median": 512.0},
                            "frameDurationCpuMs": {
                                "P50": 6.5,
                                "P90": 9.0,
                                "P95": 10.0,
                                "P99": 12.0,
                            },
                            "totalRunTimeNs": 1000,
                        }
                    },
                }
            ],
        }
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp)
            write_session_artifacts(out, session)
            self.assertTrue((out / "session.json").exists())
            self.assertTrue((out / "summary.md").exists())
            loaded = json.loads((out / "session.json").read_text())
            self.assertEqual(loaded["profile"], "confirmation")

    def test_extract_marks_frame_overrun_unavailable_below_api_31(self):
        metrics = extract_variant_metrics(
            FIXTURE,
            benchmark_name="scrollGridWithWildClickable",
            api_level=28,
        )
        self.assertEqual(
            metrics["frameOverrunMs"],
            {"available": False, "reason": "api_lt_31"},
        )
        self.assertNotIn("P50", metrics["frameOverrunMs"])

    def test_extract_retains_frame_overrun_when_present(self):
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "with_overrun.json"
            data = json.loads(FIXTURE.read_text())
            sampled = data["benchmarks"][0]["sampledMetrics"]
            sampled["frameOverrunMs"] = {
                "P50": 1.5,
                "P90": 2.0,
                "P95": 2.5,
                "P99": 3.0,
            }
            path.write_text(json.dumps(data))
            metrics = extract_variant_metrics(
                path,
                benchmark_name="scrollGridWithWildClickable",
                api_level=31,
            )
            self.assertEqual(
                metrics["frameOverrunMs"],
                {"available": True, "P50": 1.5, "P90": 2.0, "P95": 2.5, "P99": 3.0},
            )

    def test_summary_includes_workload_and_source_strategy_metadata(self):
        session = {
            "profile": "confirmation",
            "device": {"model": "AFTR", "androidVersion": "9", "apiLevel": 28},
            "gitSha": "abc1234",
            "composeVersion": "1.11.1",
            "workload": "scroll_grid",
            "sourceStrategy": "explicit",
            "compilationMode": "Partial",
            "variants": ["wild_clickable", "wild_lambda"],
            "invocations": [
                {
                    "index": 1,
                    "order": ["wild_lambda", "wild_clickable"],
                    "results": {
                        "wild_clickable": {
                            "frameCount": {"median": 800.0},
                            "frameDurationCpuMs": {
                                "P50": 5.0,
                                "P90": 6.0,
                                "P95": 7.0,
                                "P99": 8.0,
                            },
                            "frameOverrunMs": {
                                "available": False,
                                "reason": "api_lt_31",
                            },
                            "totalRunTimeNs": 1000,
                        },
                        "wild_lambda": {
                            "frameCount": {"median": 801.0},
                            "frameDurationCpuMs": {
                                "P50": 5.1,
                                "P90": 6.1,
                                "P95": 7.1,
                                "P99": 8.1,
                            },
                            "frameOverrunMs": {
                                "available": False,
                                "reason": "api_lt_31",
                            },
                            "totalRunTimeNs": 1100,
                        },
                    },
                }
            ],
        }
        summary = build_session_summary(session)
        self.assertIn("scroll_grid", summary)
        self.assertIn("explicit", summary)
        self.assertIn("frameoverrunms", summary.lower().replace(" ", ""))
        self.assertIn("unavailable", summary.lower())

    def test_comparison_supports_arbitrary_baseline_candidate_pairs(self):
        baseline = {
            "wild_clickable": {
                "frameDurationCpuMs": {"P50": 5.0, "P90": 6.0, "P95": 7.0, "P99": 8.0},
            }
        }
        candidate = {
            "wild_lambda": {
                "frameDurationCpuMs": {"P50": 5.5, "P90": 6.5, "P95": 7.5, "P99": 8.5},
            }
        }
        summary = build_comparison_summary(
            baseline_label="wild_clickable",
            candidate_label="wild_lambda",
            baseline_metrics=baseline["wild_clickable"],
            candidate_metrics=candidate["wild_lambda"],
        )
        self.assertIn("wild_clickable", summary)
        self.assertIn("wild_lambda", summary)
        self.assertIn("+10.0%", summary)

    def test_rejects_incompatible_sessions(self):
        baseline = {
            "device": {"model": "AFTR", "apiLevel": 28},
            "compilationMode": "Partial",
            "workload": "scroll_grid",
            "sourceStrategy": "explicit",
            "profile": "confirmation",
        }
        candidate = {
            "device": {"model": "AFTR", "apiLevel": 31},
            "compilationMode": "Partial",
            "workload": "scroll_grid",
            "sourceStrategy": "explicit",
            "profile": "confirmation",
        }
        error = session_compatibility_error(baseline, candidate)
        self.assertIsNotNone(error)
        self.assertIn("api", error.lower())

        compatible = dict(baseline)
        self.assertIsNone(session_compatibility_error(baseline, compatible))

    def test_rejects_sessions_with_mismatched_profiles(self):
        baseline = {
            "device": {"model": "AFTR", "apiLevel": 28},
            "compilationMode": "Partial",
            "workload": "scroll_grid",
            "sourceStrategy": "explicit",
            "profile": "confirmation",
        }
        candidate = dict(baseline)
        candidate["profile"] = "local_short"
        error = session_compatibility_error(baseline, candidate)
        self.assertIsNotNone(error)
        self.assertIn("profile", error.lower())
        self.assertIn("confirmation", error)
        self.assertIn("local_short", error)

    def test_session_invalid_reason_reports_missing_variant_archive(self):
        session = {
            "variants": ["wild_clickable", "wild_lambda"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "wild_clickable": {
                            "frameCount": {"median": 512.0},
                            "frameDurationCpuMs": {
                                "P50": 6.5,
                                "P90": 9.0,
                                "P95": 10.0,
                                "P99": 12.0,
                            },
                        },
                    },
                }
            ],
        }
        with tempfile.TemporaryDirectory() as tmp:
            session_dir = Path(tmp)
            inv = session_dir / "invocations" / "01" / "wild_clickable"
            inv.mkdir(parents=True)
            (inv / "benchmarkData.json").write_text("{}")
            reason = session_invalid_reason(session_dir, session)
            self.assertIsNotNone(reason)
            self.assertIn("missing completion markers", reason.lower())
            self.assertIn("wild_lambda", reason)

    def test_session_invalid_reason_accepts_complete_archives(self):
        session = {
            "variants": ["wild_clickable"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "wild_clickable": {
                            "frameCount": {"median": 512.0},
                            "frameDurationCpuMs": {
                                "P50": 6.5,
                                "P90": 9.0,
                                "P95": 10.0,
                                "P99": 12.0,
                            },
                        },
                    },
                }
            ],
        }
        with tempfile.TemporaryDirectory() as tmp:
            session_dir = Path(tmp)
            inv = session_dir / "invocations" / "01" / "wild_clickable"
            inv.mkdir(parents=True)
            (inv / "benchmarkData.json").write_text("{}")
            self.assertIsNone(session_invalid_reason(session_dir, session))

    def test_write_session_artifacts_rejects_invalid_session_for_lean_baseline(self):
        session = {
            "profile": "confirmation",
            "device": {"model": "AFTR", "androidVersion": "9", "apiLevel": 28},
            "gitSha": "deadbeef",
            "composeVersion": "1.11.1",
            "workload": "scroll_grid",
            "sourceStrategy": "explicit",
            "compilationMode": "Partial",
            "valid": False,
            "invalidReason": "missing completion markers for wild_lambda",
            "variants": ["wild_clickable"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "wild_clickable": {
                            "frameCount": {"median": 512.0},
                            "frameDurationCpuMs": {
                                "P50": 6.5,
                                "P90": 9.0,
                                "P95": 10.0,
                                "P99": 12.0,
                            },
                            "frameOverrunMs": {
                                "available": False,
                                "reason": "api_lt_31",
                            },
                            "totalRunTimeNs": 1000,
                        }
                    },
                }
            ],
        }
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp)
            lean = out / "lean"
            with self.assertRaisesRegex(ValueError, "missing completion markers"):
                write_session_artifacts(out, session, lean_baseline_dir=lean)
            self.assertTrue((out / "session.json").exists())
            self.assertFalse(lean.exists())

    def test_write_session_artifacts_copies_lean_variant_files_when_valid(self):
        session = {
            "profile": "confirmation",
            "device": {"model": "AFTR", "androidVersion": "9", "apiLevel": 28},
            "gitSha": "deadbeef",
            "composeVersion": "1.11.1",
            "workload": "scroll_grid",
            "sourceStrategy": "explicit",
            "compilationMode": "Partial",
            "valid": True,
            "variants": ["wild_clickable"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "wild_clickable": {
                            "frameCount": {"median": 512.0},
                            "frameDurationCpuMs": {
                                "P50": 6.5,
                                "P90": 9.0,
                                "P95": 10.0,
                                "P99": 12.0,
                            },
                            "frameOverrunMs": {
                                "available": False,
                                "reason": "api_lt_31",
                            },
                            "totalRunTimeNs": 1000,
                        }
                    },
                }
            ],
        }
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "session"
            lean = Path(tmp) / "lean"
            variant = out / "invocations" / "01" / "wild_clickable"
            variant.mkdir(parents=True)
            (variant / "benchmarkData.json").write_text('{"ok": true}\n')
            (variant / "message.txt").write_text("done\n")
            write_session_artifacts(out, session, lean_baseline_dir=lean)
            self.assertTrue((lean / "session.json").exists())
            self.assertTrue((lean / "summary.md").exists())
            self.assertTrue(
                (lean / "invocations" / "01" / "wild_clickable" / "benchmarkData.json").exists()
            )
            self.assertTrue(
                (lean / "invocations" / "01" / "wild_clickable" / "message.txt").exists()
            )


if __name__ == "__main__":
    unittest.main()
