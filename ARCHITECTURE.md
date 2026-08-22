# UQX Native App — Architecture

## High-level system

```text
                     UQX Native Android App
                              │
              ┌───────────────┴────────────────┐
              │                                │
              ▼                                ▼
       Account / Rewards                 Self-Custody Wallet
              │                                │
              │ HTTPS                          │ local cryptography
              ▼                                ▼
        UQX API Backend                BIP39 / BIP32 / secp256k1
              │                                │
    ┌─────────┼─────────┐                      │
    │         │         │                      ▼
    ▼         ▼         ▼                Android Keystore-backed
 Mining    Referral   Account              encrypted storage
 Sessions  Network    Transfers                   │
    │         │         │                         │
    └─────────┴─────────┘                         ▼
              │                           BNB Smart Chain address
              │                                  │
              │                                  │ read-only JSON-RPC
              │                                  ▼
              │                           UQX / Presale contracts
              │
              ▼
     Notifications / History
```

## Android client layers

```text
Jetpack Compose UI
        │
        ├── Dashboard
        ├── Referral Center
        ├── Wallet
        ├── Notifications
        ├── Profile / Settings
        ├── 2FA / Recovery
        └── History / Leaderboard
        │
        ▼
Client services / stores
        │
        ├── Retrofit API client
        ├── encrypted token store
        ├── encrypted wallet store
        ├── Play Install Referrer
        ├── Firebase Messaging
        └── BNB read-only RPC
```

## Trust boundaries

### Account backend

Trusted for:

- reward-session state;
- referral accounting;
- authenticated account balance;
- in-app transfer accounting;
- notifications and session state.

Not trusted with:

- wallet mnemonic;
- wallet private key.

### Android device

Trusted with:

- local wallet key material;
- device authentication gate;
- rendering the recovery phrase to the user.

Main risks include device compromise, malicious accessibility/screen capture environments, insecure user backup of the phrase and compromised dependencies. Device-side controls reduce risk but do not eliminate it.

### BNB RPC

Used for read-only public blockchain state. The current read client does not sign transactions. RPC unavailability is surfaced instead of translated into a false zero-balance state.

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
EVM key derivation
    │
    ▼
Store locally with Keystore-backed encryption
    │
    ├── show phrase for backup
    │
    └── register/use public address where product workflow requires it
```

The portable recovery asset is the mnemonic, not the encrypted preference file.

## Rewards lifecycle

```text
Start session
    │
    ▼
Backend records active period
    │
    ▼
Client renders countdown/progress
    │
    ▼
Backend finalizes reward
    │
    ▼
Account balance/history update
```

This is a community/reward mechanism and should not be confused with proof-of-work consensus mining.

## Referral lifecycle

```text
User receives referral code/link
        │
        ▼
Link routes through Play Store
        │
        ▼
Install Referrer attribution
        │
        ▼
Referral relationship recorded by backend
        │
        ▼
Network / tier / reward statistics
```

## Public repository policy

This architecture repository intentionally documents interfaces and trust boundaries while leaving live source, production credentials, operational anti-abuse controls and user data private.
