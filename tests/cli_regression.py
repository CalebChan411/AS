"""Exercise Main through stdin. Run after compiling Java classes into build/."""
from pathlib import Path
import subprocess


PROJECT = Path(__file__).resolve().parent.parent
CHECKS = 0


def run(text):
    result = subprocess.run(
        ["java", "-cp", "build", "Main"], cwd=PROJECT,
        input=text, text=True, capture_output=True, timeout=10,
    )
    if result.returncode != 0 or result.stderr:
        raise AssertionError("Main failed: " + result.stderr + result.stdout)
    return result.stdout


def check(condition, message):
    global CHECKS
    CHECKS += 1
    if not condition:
        raise AssertionError(message)


def in_order(output, fragments):
    position = 0
    for fragment in fragments:
        found = output.find(fragment, position)
        check(found >= 0, "Missing output in order: " + fragment + "\n" + output)
        position = found + len(fragment)


def main():
    sample = run((PROJECT / "sample_input.txt").read_text())
    check(sample.count(" is undone.") == 3, "sample performs three undos")
    check(sample.count(" is redone.") == 3, "sample performs three redos")
    in_order(sample, [
        "Delivery Van Fleet CENTRAL EXPRESS (F001)",
        "d001, Alex Chan", "d002, Jamie Lee",
        "Refrigerated Truck Fleet FROST LINE (F102)",
        "d101, Morgan Wong", "d102, Taylor Lam",
        "Fleet F999 is not found!", "Driver d999 is not found",
        "Invalid command!", "Thank you for using SDFMS.",
    ])

    no_current = run("u\nr\na\nm\nsf\nsa\nd\nMissing\ncn\nName\ns\nMissing\nl\nx\n")
    check(no_current.count("No current fleet selected.") == 5,
          "five current-fleet operations reject missing selection")
    in_order(no_current, ["Nothing to undo.", "Nothing to redo.",
                          "No fleets available.", "Fleet Missing is not found!",
                          "Undo List", "Redo List", "System terminated."])

    malformed = run(
        "c\nv\nA\nAlpha\na\nwrong format\na\nD1, Alex\nabc\n"
        "a\nD1, Alex\n1\nm\nD1\n2\nu\n"
        "a\nD2, Other\nabc\nm\nD1\n9\na\nd1, Duplicate\n1\nq\n"
        "sf\nr\nsf\nx\n"
    )
    check(malformed.count("Driver is added.") == 1, "malformed records never add a driver")
    check(malformed.count("Invalid driver role. Please enter 1 or 2.") == 2,
          "nonnumeric role input is handled without losing command parsing")
    in_order(malformed, [
        "Please enter driver information in the format: ID, Name.",
        "Invalid driver role. Please enter 1 or 2.",
        "Driver is added.", "Driver role is updated.", " is undone.",
        "Invalid driver role. Please enter 1 or 2.",
        "Invalid driver role for delivery van fleet.", "already exists", "Invalid command!",
        "Delivery Van Fleet Alpha (A)\n\nVan Driver:\n\nD1, Alex",
        " is redone.", "Delivery Van Fleet Alpha (A)\n\nVan Driver: NIL",
        "Courier Driver:\n\nD1, Alex", "System terminated.",
    ])

    cross_fleet = run(
        "c\nv\nA\nAlpha\na\nD1, Alex\n1\n"
        "c\nr\nB\nBeta\na\nD1, Blair\n2\n"
        "s\nA\nm\nD1\n2\ns\nB\nu\nsf\ns\nA\nsf\nr\nsf\nx\n"
    )
    in_order(cross_fleet, [
        "Driver role is updated.", "Current fleet is changed to B.", " is undone.",
        "Refrigerated Truck Fleet Beta (B)", "Refrigerated-Truck Driver: NIL",
        "Cold-Chain Delivery Driver:\n\nD1, Blair",
        "Delivery Van Fleet Alpha (A)\n\nVan Driver:\n\nD1, Alex",
        " is redone.", "Delivery Van Fleet Alpha (A)\n\nVan Driver: NIL",
        "Courier Driver:\n\nD1, Alex", "System terminated.",
    ])

    base = "c\nv\nA\nAlpha\n"
    minimum_role = run(base + "a\nD1, Alex\n1\na\nD2, Other\n-2147483648\n"
                       "m\nD1\n-2147483648\nsf\nx\n")
    check(minimum_role.count("Invalid driver role for delivery van fleet.") == 2,
          "minimum integer role is rejected explicitly for add and modify")
    check("D2, Other" not in minimum_role, "minimum integer role never inserts a driver")
    in_order(minimum_role, ["Van Driver:\n\nD1, Alex", "Courier Driver: NIL"])

    prefixes = ["", "c\n", "c\nv\n", "c\nv\nA\n",
                base + "a\n", base + "a\nD1, Alex\n",
                base + "m\n", base + "m\nD1\n", base + "d\n",
                base + "cn\n", base + "s\n"]
    for prefix in prefixes:
        output = run(prefix)
        check("Exception" not in output, "EOF at every partial prompt exits cleanly")
    print("PASS: " + str(CHECKS) + " CLI checks, including 11 partial-input EOF cases.")


if __name__ == "__main__":
    main()
