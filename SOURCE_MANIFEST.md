# Production Source Manifest

This manifest records the exact private-production blobs that were reviewed and approved for public release in this repository.

Private source repository: `umarae-dev/uqxnative-fruntend` (private)

| Public file | Private production path | Private blob SHA | Publication status |
| --- | --- | --- | --- |
| `production-safe/android/UqxWalletCrypto.kt` | `app/src/main/java/com/umartech/umarae/crypto/UqxWalletCrypto.kt` | `9581b1108d2fc3ff8de82a926e1e4722e1d0247b` | Exact production-safe source |
| `production-safe/android/UqxWalletStore.kt` | `app/src/main/java/com/umartech/umarae/data/UqxWalletStore.kt` | `4a4dd4c004b253f97e070b43dc3d15a15a0ccd0f` | Exact production-safe source |
| `production-safe/android/TokenStore.kt` | `app/src/main/java/com/umartech/umarae/data/TokenStore.kt` | `d9528c08e4fab4c8ff740585cb58180258759c15` | Exact production-safe source |
| `production-safe/android/backup_rules.xml` | `app/src/main/res/xml/backup_rules.xml` | `9605bcff3a796539599fc955482b35e5b7c0fcac` | Exact production-safe source |
| `production-safe/android/data_extraction_rules.xml` | `app/src/main/res/xml/data_extraction_rules.xml` | `e8a9135057ab21a8bf0a12a8d6f777e1d32e4076` | Exact production-safe source |

`BiometricAuth.kt` is published as a production-derived public-safe variant with comments shortened for public review; the private production blob reviewed was `52f5a746b01345fb28d974f9214aac3c2a1f21e9`.

## Intentionally not copied

The private application also contains networking, API models, Firebase/push integration, Google sign-in, UI/navigation, install attribution and a read-only BSC RPC client. Those files were not blindly mirrored.

In particular, `BscRpc.kt` contains deployed UQX contract addresses. The canonical contract/deployment evidence belongs in `umarae-dev/uqx-bnb-contracts-overview`, so this repository documents the read-only chain behavior without duplicating the contract evidence source of truth.

No `google-services.json`, signing keystore, OAuth credential, production API secret, access token, seed phrase, private key, user record or private environment file is approved for public release.
