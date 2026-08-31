# UQX Native App — Wallet Architecture

## High-level system

```text
                     UQX Native Android App
                              │
                              ▼
                     Self-Custody Wallet
                              │
              ┌───────────────┼────────────────┐
              │               │                │
              ▼               ▼                ▼
       Local cryptography   Device security   BNB read-only RPC
              │               │                │
              ▼               ▼                ▼
      BIP39 / BIP32 /      Android          UQX token /
        secp256k1           Keystore          presale state
              │               │
              └───────┬───────┘
                      ▼
              Device-owned wallet
```

The current UQX product is the self-custody Web3 wallet layer of the Zynost ecosystem. Older reward/mining/referral surfaces in the wider private repository are legacy implementation material and do not define the current product category.

## Native client layers

```text
Jetpack Compose UI
        │
        ├── Wallet-first home
        ├── UQX Wallet
        ├── Receive / QR
        ├── On-chain position
        ├── Notifications
        ├── Profile / Settings
        ├── 2FA / Recovery
        └── Active sessions
        │
        ▼
Client services / stores
        │
        ├── BIP39/BIP32 wallet generator
        ├── encrypted wallet store
        ├── encrypted auth-token store
        ├── biometric/device authentication
        ├── application API client
        └── BNB read-only RPC
```

## Trust boundaries

### Wallet device boundary

The Android device is responsible for:

- generating the wallet recovery phrase and EVM keypair;
- storing wallet credentials in Android Keystore-backed encrypted preferences;
- rendering the recovery phrase for user backup;
- gating sensitive wallet actions with device authentication where supported.

The recovery phrase is the portable recovery credential. A person who obtains it may be able to restore the wallet on another compatible EVM wallet. Device-side protections are therefore defense in depth, not recovery-phrase compromise immunity.

### Application backend

The application backend may handle account-level services such as authentication, settings, notifications or other connected application state, but it is not the custody authority for the native wallet's mnemonic/private key.

### BNB Smart Chain RPC

The reviewed client uses public chain reads for supported UQX/presale state. RPC unavailability should surface as unavailable/offline state rather than being converted into a false zero balance.

The current reviewed BNB client does not establish a public claim of arbitrary transaction signing/broadcasting from the native application.

## Wallet lifecycle

```text
Create wallet
    │
    ▼
SecureRandom entropy
    │
    ▼
12-word BIP39 mnemonic
    │
    ▼
BIP32 EVM derivation
    │
    ▼
secp256k1 keypair + address
    │
    ▼
Keystore-backed encrypted local storage
    │
    ├── recovery-phrase backup
    └── BNB Smart Chain address
             │
             ▼
      supported on-chain reads
```

## On-chain position flow

```text
Device-owned address
      │
      ▼
BNB JSON-RPC eth_call
      │
      ├── UQX balance
      ├── presale purchased
      ├── presale vested
      ├── presale locked
      └── presale claimable
```

The chain remains the source of truth for these supported on-chain measurements.

## Legacy compatibility boundary

The private production history contains older account/reward/referral/mining routes and UI components. During migration these names may remain in code or APIs so old application/backend contracts are not broken abruptly.

They should not appear as:

- current product positioning;
- primary navigation;
- app-store copy;
- website feature claims;
- investor/startup-visa product descriptions.

Removal of legacy APIs should be handled as a separate compatibility migration after clients and operational dependencies are confirmed.

## Public repository policy

This repository publishes the wallet/security implementation subset and the trust model needed for review while keeping production credentials, user data, signing material and private operational configuration outside public source control.
