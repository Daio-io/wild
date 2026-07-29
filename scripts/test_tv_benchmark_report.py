#!/usr/bin/env python3
import json
import tempfile
import unittest
from pathlib import Path

from tv_benchmark_report import (
    build_session_summary,
    extract_variant_metrics,
    percent_delta,
    write_session_artifacts,
)

FIXTURE = Path(__file__).parent / "testdata" / "aftr_sample_benchmarkData.json"


class TvBenchmarkReportTest(unittest.TestCase):
    def test_extract_variant_metrics_reads_frame_and_memory(self):
        metrics = extract_variant_metrics(FIXTURE, benchmark_name="scrollGridWithCurrentTraversal")
        self.assertEqual(metrics["frameCount"]["median"], 831.0)
        self.assertAlmostEqual(metrics["frameDurationCpuMs"]["P50"], 5.902167, places=5)
        self.assertEqual(metrics["memoryHeapSizeMaxKb"]["median"], 9288.0)
        self.assertGreater(metrics["totalRunTimeNs"], 0)

    def test_percent_delta_handles_baseline(self):
        self.assertAlmostEqual(percent_delta(9.0, 12.0), -25.0)
        self.assertIsNone(percent_delta(1.0, 0.0))

    def test_summary_includes_deltas_and_human_verdict_note(self):
        session = {
            "profile": "local_short",
            "device": {"model": "AFTR", "androidVersion": "9"},
            "gitSha": "abc1234",
            "composeVersion": "1.11.1",
            "variants": ["current_traversal", "candidate_composite", "material_surface"],
            "invocations": [
                {
                    "index": 1,
                    "results": {
                        "current_traversal": extract_variant_metrics(
                            FIXTURE, "scrollGridWithCurrentTraversal"
                        ),
                        "candidate_composite": {
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
        self.assertIn("candidate_composite", summary)
        self.assertIn("human-reviewed", summary.lower())
        self.assertIn("P99", summary)

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


if __name__ == "__main__":
    unittest.main()
