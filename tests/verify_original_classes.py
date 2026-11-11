"""Compare protected Java sources byte for byte with the supplied prototype ZIP.

Usage: python tests/verify_original_classes.py path/to/original-prototype.zip
"""
from pathlib import Path
import sys
import zipfile


def main():
    if len(sys.argv) != 2:
        raise SystemExit("Usage: python tests/verify_original_classes.py ORIGINAL_ZIP")
    project = Path(__file__).resolve().parent.parent
    protected = (
        "Driver.java", "VehicleFleet.java",
        "DeliveryVanFleet.java", "RefrigeratedTruckFleet.java",
    )
    with zipfile.ZipFile(sys.argv[1]) as archive:
        for name in protected:
            matches = [entry for entry in archive.namelist()
                       if not entry.endswith("/") and Path(entry).name == name]
            if len(matches) != 1:
                raise AssertionError("Expected exactly one original " + name)
            if (project / name).read_bytes() != archive.read(matches[0]):
                raise AssertionError("Protected file changed: " + name)
            print("UNCHANGED: " + name)
    print("PASS: all four protected files match the uploaded ZIP byte for byte.")


if __name__ == "__main__":
    main()
