# UQX — Self-Custody Web3 Wallet

> **Production-derived Android wallet security architecture for BNB Smart Chain.**

UQX is the self-custody Web3 wallet and token-utility layer of the Zynost ecosystem. The native Android application generates an EVM wallet on-device, keeps wallet credentials in Android Keystore-backed encrypted storage, and exposes supported BNB Smart Chain UQX/presale state without making a Zynost server the custodian of the user's recovery phrase or private key.

**Production stack:** Kotlin + Jetpack Compose  
**Private production repository:** `uqxnative-fruntend`  
**Public repository:** selected production-safe wallet/security source + architecture documentation  
**BNB contracts/evidence source of truth:** [`uqx-bnb-contracts-overview`](https://github.com/umarae-dev/uqx-bnb-contracts-overview)

## Reviewer start here

The strongest evidence in this repository is the production-derived source under [`production-safe/android/`](production-safe/android/) and its lineage in [`SOURCE_MANIFEST.md`](SOURCE_MANIFEST.md).

Published production-safe modules include:

- `UqxWalletCrypto.kt` — BIP39/BIP32/secp256k1 EVM wallet generation;
- `UqxWalletStore.kt` — Android Keystore-backed encrypted mnemonic/private-key storage;
- `TokenStore.kt` — encrypted authentication storage kept separate from wallet storage;
- `BiometricAuth.kt` — device biometric/PIN gate used around sensitive wallet actions;
- `backup_rules.xml` — backup exclusions for encrypted auth/wallet preferences;
- `data_extraction_rules.xml` — cloud-backup/device-transfer exclusions for those encrypted stores.

The complete commercial Android source remains private because it contains live API wiring, Firebase/Google configuration surfaces, operational integration and unreleased application code that are not required to inspect the wallet trust boundary.

## Current product boundary

```text
UQX Native Android App
        │
        ▼
Self-Custody Wallet
        │
        ├── BIP39 recovery phrase generated on-device
        ├── BIP32 EVM derivation m/44'/60'/0'/0/0
        ├── secp256k1 keypair + BNB Smart Chain address
        ├── Android Keystore-backed encrypted local storage
        ├── biometric / device-credential protection
        ├── receive address + QR
        └── read-only BNB Smart Chain position visibility
                 │
                 ├── UQX balance
                 ├── presale purchased
                 ├── vested / locked
                 └── currently claimable
```

Older account, referral, leaderboard and engagement/mining code exists in the wider private codebase as legacy/backwards-compatibility material. Those systems are **not the current UQX product identity** and are intentionally excluded from this wallet-first public positioning.

## Wallet cryptography

The published `UqxWalletCrypto.kt` is production-derived. It uses:

- 128-bit `SecureRandom` entropy;
- BIP39 12-word mnemonic generation;
- BIP32 key derivation;
- standard EVM path `m/44'/60'/0'/0/0`;
- secp256k1 credentials/address generation.

Wallet generation does not require a Zynost/UQX server to create the mnemonic or private key.

## Wallet storage

`UqxWalletStore.kt` stores wallet material in `EncryptedSharedPreferences` using an Android Keystore-backed AES-256-GCM master key. The production-safe source shows the stored fields:

- wallet address;
- mnemonic;
- private key;
- last wallet-unlock timestamp.

Backup/data-extraction rules exclude the encrypted wallet preference store from normal cloud backup and device transfer because its Android Keystore material is device-bound. The recovery phrase remains the portable recovery mechanism.

## Authentication and wallet separation

Authentication state and self-custody wallet state are separate security domains. `TokenStore.kt` can recover from damaged/restored authentication preferences without deleting `uqx_wallet_prefs`.

This matters because losing an application login should not silently mean deleting the wallet's locally protected key material.

## Device authentication

The published biometric helper can request biometric or device-credential verification around sensitive application actions where supported.

This is defense in depth. It is **not** a claim that a standard BIP39 wallet remains safe after its recovery phrase is disclosed. Anyone who obtains a valid recovery phrase may be able to restore the same wallet elsewhere.

## BNB Smart Chain visibility

The private production application includes a read-only BNB JSON-RPC client that uses `eth_call` to inspect supported UQX and presale state for the device-owned wallet address.

Canonical deployed addresses are deliberately kept in the UQX BNB contracts repository rather than duplicated as a second source of truth here.

```text
Device-owned wallet address
        │
        ▼
BNB Smart Chain read-only RPC
        │
        ├── token balance
        ├── presale purchased
        ├── vested / locked
        └── claimable state
```

The currently reviewed BNB client is read-only. This repository therefore does **not** advertise arbitrary native-app transaction signing/broadcasting as an implemented capability.

## Retired terminology

The following terms describe older product experiments or legacy implementation names and should not be used as current UQX branding:

- mining app;
- mobile mining;
- mining rewards;
- reward sessions;
- reward network;
- earn-by-tapping positioning.

Internal legacy class/API names may survive temporarily during migration so existing code can be retired safely rather than renamed destructively.

## Security boundaries

Public here:

- selected production-derived wallet/security source;
- exact private-source lineage;
- architecture and threat-boundary documentation;
- CI guard against accidental credential/keystore/config publication.

Not public here:

- `google-services.json`;
- signing keystores;
- OAuth client secrets;
- production access tokens;
- any user recovery phrase/private key;
- private backend configuration;
- unreleased product code;
- customer/user data.

## CI

Every push and pull request runs the public-source guard. It checks for forbidden secret-bearing files, obvious credential material, required published source files and valid backup/data-extraction XML.

This subset is intentionally not presented as the complete commercial Android application. The published modules are independently inspectable and traceable to the private production source.

## Production lineage

The private native application predates this public release. Public commit dates represent the safe-publication timeline, not the beginning of UQX product development.

See [`SOURCE_MANIFEST.md`](SOURCE_MANIFEST.md), [`ARCHITECTURE.md`](ARCHITECTURE.md) and [`SECURITY.md`](SECURITY.md).

## Claim discipline

Public documentation should use precise terms such as **self-custody**, **device-owned keys**, **on-device wallet generation**, **on-chain state**, **encrypted local storage** and **source-backed**. Avoid credibility adjectives such as “real” where the more precise technical term is available.
