from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
FORBIDDEN_NAMES = {".env", "google-services.json", "local.properties", "keystore.properties"}
PATTERNS = [
    re.compile(r"-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----"),
    # Match likely literal credentials, not harmless identifiers such as
    # KEY_PRIVATE_KEY = "wallet_private_key" used by encrypted local storage.
    re.compile(
        r"(?i)(?:client[_-]?secret|api[_-]?secret|api[_-]?key|access[_-]?token|refresh[_-]?token)"
        r"\s*[=:]\s*['\"](?!(?:replace|example|placeholder|your[-_]))[^'\"]{16,}['\"]"
    ),
]

errors = []
for path in ROOT.rglob("*"):
    if not path.is_file() or ".git" in path.parts:
        continue
    if path.name in FORBIDDEN_NAMES or path.suffix.lower() in {".jks", ".keystore", ".p12", ".pem"}:
        errors.append(f"forbidden public file: {path.relative_to(ROOT)}")
        continue
    if path.suffix.lower() not in {".kt", ".xml", ".md", ".py", ".yml", ".yaml"}:
        continue
    text = path.read_text(encoding="utf-8", errors="ignore")
    for pattern in PATTERNS:
        if pattern.search(text):
            errors.append(f"possible credential material: {path.relative_to(ROOT)}")

for xml_path in [
    ROOT / "production-safe/android/backup_rules.xml",
    ROOT / "production-safe/android/data_extraction_rules.xml",
]:
    try:
        ET.parse(xml_path)
    except Exception as exc:
        errors.append(f"invalid XML {xml_path.relative_to(ROOT)}: {exc}")

required = [
    ROOT / "production-safe/android/UqxWalletCrypto.kt",
    ROOT / "production-safe/android/UqxWalletStore.kt",
    ROOT / "production-safe/android/TokenStore.kt",
    ROOT / "production-safe/android/BiometricAuth.kt",
    ROOT / "SOURCE_MANIFEST.md",
]
for path in required:
    if not path.exists():
        errors.append(f"missing required public file: {path.relative_to(ROOT)}")

if errors:
    print("\n".join(errors))
    sys.exit(1)

print("public repository guard passed")
