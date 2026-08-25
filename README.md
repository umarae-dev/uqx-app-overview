# UQX — Native Android Rewards & Self-Custody Wallet

> Native Android UQX client architecture plus a production-safe source subset copied from the private production application.

UQX is the consumer/community application for the UQX ecosystem. It combines server-accounted engagement rewards and referrals with a separate on-device self-custody BNB Smart Chain wallet.

**Production stack:** Kotlin + Jetpack Compose  
**Private production repository:** `uqxnative-fruntend`  
**Public repository:** selected production-safe source + architecture/security documentation  
**BNB contracts/evidence source of truth:** [`uqx-bnb-contracts-overview`](https://github.com/umarae-dev/uqx-bnb-contracts-overview)

## Reviewer start here

The strongest evidence in this repository is not the product description. It is the production-derived source under [`production-safe/android/`](production-safe/android/) and its exact lineage in [`SOURCE_MANIFEST.md`](SOURCE_MANIFEST.md).

Published production-safe modules include:

- `UqxWalletCrypto.kt` — real BIP39/BIP32/secp256k1 EVM wallet generation;
- `UqxWalletStore.kt` — Android Keystore-backed encrypted mnemonic/private-key storage;
- `TokenStore.kt` — encrypted auth-token storage with recovery from corrupted/restored auth preferences without touching wallet storage;
- `BiometricAuth.kt` — production-derived device biometric/PIN gate for sensitive wallet actions;
- `backup_rules.xml` — legacy Android backup exclusions for encrypted auth/wallet preferences;
- `data_extraction_rules.xml` — cloud-backup and device-transfer exclusions for those encrypted preference stores.

The complete commercial Android source tree remains private because it also contains live API wiring, Firebase/Google configuration surfaces, unreleased UI and operational integration that are not required to inspect these wallet/security invariants.

## Account layer vs self-custody wallet

```text
UQX Android App
  │
  ├── Account layer (backend-owned state)
  │     ├── engagement reward sessions
  │     ├── referral rewards
  │     ├── internal UQX balance
  │     └── internal user-to-user transfers
  │
  └── Self-custody wallet (device-owned secrets)
        ├── BIP39 recovery phrase
        ├── m/44'/60'/0'/0/0 derivation
        ├── secp256k1 EVM keypair
        ├── BNB Smart Chain address
        └── direct read-only UQX/presale state
```

These are intentionally different trust models. The account balance is server accounting; the wallet is a real EVM address whose mnemonic/private key are generated and stored on the Android device.

## Production wallet cryptography

The public `UqxWalletCrypto.kt` is copied from the private production app. It uses:

- 128-bit `SecureRandom` entropy;
- BIP39 12-word mnemonic generation;
- BIP32 key derivation;
- standard EVM path `m/44'/60'/0'/0/0`;
- secp256k1 credentials/address generation.

Wallet generation does not require a Zynost/UQX server to create the mnemonic or private key.

## Production wallet storage

`UqxWalletStore.kt` stores wallet material in `EncryptedSharedPreferences` using an Android Keystore-backed AES-256-GCM master key. The production-safe source shows the actual stored fields:

- address;
- mnemonic;
- private key;
- last wallet-unlock timestamp.

The public backup/data-extraction rules also show that encrypted auth and wallet preference blobs are excluded from cloud backup and device-to-device transfer because their Android Keystore keys are device-bound.

That makes the recovery phrase—not a copied encrypted preferences file—the portable recovery mechanism.

## Auth-storage hardening

`TokenStore.kt` demonstrates a separate security rule: if encrypted authentication preferences become unreadable after restore/corruption, the app can discard and recreate only the disposable auth store. It deliberately does **not** delete `uqx_wallet_prefs`.

This production fix is useful reviewer evidence because it shows wallet and login state are treated as different security domains.

## Device authentication

The published production-derived biometric helper gates sensitive wallet actions behind biometric or device credential authentication where available, with compatibility handling across Android API levels.

It is defense in depth around a local secret, not a claim that the wallet is immune to device compromise.

## Direct BNB Chain reads

The private production app also includes a read-only BNB JSON-RPC client that uses `eth_call` to read the user's current UQX token/presale state.

That file is deliberately **not duplicated here** because it hardcodes deployed UQX contract addresses. Canonical contract addresses, deployment state and transaction evidence belong in `uqx-bnb-contracts-overview` so reviewers have one source of truth.

The architectural invariant is still clear:

```text
Android wallet address
       │
       ▼
BNB Smart Chain read-only RPC
       │
       ├── UQX balance
       ├── presale purchased
       ├── claimed
       ├── claimable
       └── vested/locked state
```

The read-only client does not sign or broadcast transactions.

## Rewards/referrals

The app's 24-hour “mining” surface is an engagement/reward mechanism, not proof-of-work mining performed by the phone CPU/GPU.

The native client also supports referral install attribution, sharing, network/tier presentation, reward history, notifications, active sessions, authentication and 2FA flows. Those full application modules remain in the private production tree unless a file has been explicitly approved in `SOURCE_MANIFEST.md`.

## Security boundaries

Public here:

- selected real production wallet/security source;
- exact private-source blob lineage;
- architecture and threat-boundary documentation;
- CI guard against accidental credential/keystore/config publication.

Not public here:

- `google-services.json`;
- signing keystores;
- OAuth client secrets;
- production access tokens;
- seed phrases/private keys from any real user;
- private backend configuration;
- unreleased product code;
- anti-abuse internals;
- customer/user data.

## CI

Every push and pull request runs the public-source guard. It checks for forbidden secret-bearing file types/names, obvious credential material, required published source files and valid backup/data-extraction XML.

This public subset is intentionally not presented as a standalone full Android application build. The full production Gradle project remains private; the exact production-safe modules published here are independently inspectable and traceable to their private-source blobs.

## Production lineage

The private native application predates this public release. Public commit dates represent the safe-publication timeline, not the start of UQX product development.

See [`SOURCE_MANIFEST.md`](SOURCE_MANIFEST.md), [`ARCHITECTURE.md`](ARCHITECTURE.md) and [`SECURITY.md`](SECURITY.md).

## Status

The private production app contains native rewards/referral/account flows plus the self-custody BNB wallet. This repository now exposes a meaningful, verifiable production-safe subset instead of being documentation-only.
