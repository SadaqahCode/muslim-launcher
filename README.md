# 🌙 Muslim Launcher

> **Building the Future of 🕌 Islamic Digital Life**  
> Reclaiming Muslim attention with faith-centered technology — from daily apps to intelligent boundaries

[![Version](https://img.shields.io/badge/version-0.3.0--alpha-blue.svg)]()
[![License](https://img.shields.io/badge/license-GPL--3.0-green.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%208.0%2B-brightgreen.svg)]()

---

## 🕌 What is Muslim Launcher?

Muslim Launcher reimagines your Android home screen from an Islamic perspective.

Instead of endless app icons designed to grab your attention, Muslim Launcher creates a **digital boundary** — a home screen that helps you stay focused on what matters: Salah, beneficial activities, and reducing phone addiction.

### The Core Concept

**Traditional launchers:**

- App grids optimized for "engagement"
- No concept of prayer times or Islamic values
- Every app equally accessible (no priorities)
- Designed to maximize screen time

**Muslim Launcher:**

- Home screen organized around **Salah times**
- **Digital boundaries** - apps are categorized (beneficial, neutral, time-wasting)
- **Focus-first design** - minimal distractions, intentional access
- **Islamic calendar** as primary (Hijri date prominent)
- Integration with **Muslim Services** for automatic focus modes

### What Makes It Different

This is not just a launcher with Islamic wallpapers. This is a **fundamental rethinking** of what a phone home screen should be for a Muslim.

---

## ✨ Core Features

### 🕌 Prayer-Centered Interface

- **Prayer time countdown** prominently displayed
- **Next prayer widget** always visible
- **Auto-focus mode** 5 minutes before each Salah (integrates with Muslim Services)
- **Hijri calendar** displayed alongside Gregorian
- **Islamic date awareness** (Ramadan, Jumah, special days highlighted)

### 🚧 Digital Boundaries

**App Categorization:**
Apps are organized into three categories:

1. **Beneficial** (Green) - Quran, Hadith, educational apps, work tools
2. **Neutral** (Gray) - Utilities, phone, messages, camera
3. **Time-Wasting** (Red) - Social media, games, entertainment

**How it works:**

- User categorizes apps (or uses community suggestions)
- Beneficial apps: Always easily accessible
- Neutral apps: Normal access
- Time-wasting apps: **Intentional friction**
  - Extra tap to open ("Are you sure?")
  - Shows usage stats before opening
  - Can be blocked during focus modes

### 🧘 Focus Mode Integration

Powered by Muslim Services (if installed):

- **Prayer Mode** - Only essential apps accessible during Salah window
- **Ramadan Mode** - Time-wasting apps hidden during fasting hours
- **Night Guard** - Entertainment blocked after bedtime
- **Work Mode** - Social media hidden during work hours

If Muslim Services is not installed, basic time-based blocking available.

### 📱 Minimalist Design

**No infinite scroll. No feed. No notifications on home screen.**

- Clean, distraction-free interface
- Essential apps in quick access
- Everything else: one intentional swipe away
- No widgets pulling attention (except prayer time)
- Dark mode optimized for night use

### 🌍 Community App Curation

**Halal App Directory** (Future):

- Developers can submit apps for inclusion
- Requirements:
  - ✅ No ads (or ethical ads only)
  - ✅ No analytics/tracking
  - ✅ Privacy-respecting
  - ✅ Beneficial for Muslims
- Community votes on submissions
- Curated list shown in launcher's app drawer

---

## 🎯 The Philosophy

### Digital Boundary Concept

Your phone should have **boundaries** like your life does.

Just as you wouldn't let a stranger interrupt your prayer, your phone shouldn't either. Muslim Launcher creates **digital boundaries** that align technology with Islamic values:

1. **Time boundaries** - Prayer times are sacred, phone respects that
2. **Attention boundaries** - Beneficial apps easy, wasteful apps have friction
3. **Content boundaries** - Halal apps promoted, haram apps discouraged
4. **Privacy boundaries** - No tracking, no data mining

### What Phone Should Be

**Not:** An endless stream of notifications and distractions  
**But:** A tool that serves your goals and values

**Not:** Designed to maximize your screen time  
**But:** Designed to help you use your time wisely

**Not:** Controlled by algorithms optimizing for engagement  
**But:** Controlled by you, aligned with your faith

---

## 🔧 How It Works

### Integration with Muslim Services

Muslim Launcher works standalone, but reaches full potential with Muslim Services:

```
Muslim Launcher (UI)
    ↓
Muslim Services (Background Logic)
    ↓
Together: Complete Islamic phone experience
```

**Launcher provides:**

- Islamic home screen interface
- App categorization UI
- Quick access to beneficial apps
- Prayer time display

**Services provides:**

- Prayer time calculations
- Automatic focus mode triggers
- Notification filtering
- App usage tracking

**Together:**

- Auto-block time-wasting apps during prayer
- Show usage insights on home screen
- Seamless Ramadan mode
- Complete digital boundary system

### App Categorization System

**How apps are categorized:**

1. **Auto-categorization** (default):

   - Quran/Islamic apps → Beneficial
   - Social media/games → Time-wasting
   - Everything else → Neutral

2. **User customization**:

   - Long-press any app
   - Choose category
   - Set custom rules

3. **Community suggestions** (future):
   - See how other users categorized apps
   - Vote on community categories
   - Share your categorization

### Focus Mode Behavior

**When focus mode is active:**

- **Time-wasting apps:**

  - Hidden from home screen
  - Require extra steps to open
  - Show "Are you sure?" prompt
  - Display usage stats ("You used this 2h today")

- **Beneficial apps:**

  - Remain fully accessible
  - Promoted on home screen

- **Emergency:**
  - Phone calls always work
  - Whitelisted contacts can reach you
  - Emergency apps (hospital, bank) accessible

---

## 📥 Installation

> [!NOTE]
> Currently in early alpha. Launcher is functional but missing many planned features.

### Requirements

- Android 8.0+
- ~10MB storage
- Optional: Muslim Services for full features

### Install

**From Play Store**

```
Search "Muslim Launcher" → Install → Set as default launcher
```

### First Setup

1. Set Muslim Launcher as default
2. Grant notification permission
3. Configure prayer times (or it syncs from Muslim Services)
4. Choose calculation method
5. Categorize your apps (or use defaults)
6. Done!

---

## 🗺️ Roadmap

### Current Status (v0.3 Alpha)

- [x] Basic launcher functionality (open apps)
- [x] Prayer time widget
- [x] Hijri calendar display
- [x] App drawer with search
- [ ] App categorization system
- [ ] Focus mode integration
- [ ] Muslim Services binding

[Full Roadmap](/docs/ROADMAP.md)

---

## 🤝 For Developers & Contributors

### Submit Your App to Halal Directory

Want your app featured in Muslim Launcher's curated app drawer?

**Requirements:**

- ✅ No ads (or ethical, minimal ads)
- ✅ No analytics/tracking (or privacy-respecting only)
- ✅ Privacy policy clear and honest
- ✅ Beneficial for Muslims (educational, Islamic, productivity)
- ✅ Open source (preferred but not required)

**Prohibited:**

- ❌ Tracking/surveillance
- ❌ Data mining for ads
- ❌ Haram content
- ❌ Addictive design patterns
- ❌ Deceptive practices

[Submit Your App](https://github.com/Sadaqahcode/muslim-launcher/issues/new?template=app_submission.md)

### Contribute Code

Muslim Launcher is open source and welcomes contributors:

- Fix bugs
- Add features
- Improve UI/UX
- Optimize performance
- Write documentation

[Contributing Guide](/docs/CONTRIBUTING.md)

---

## 🔐 Privacy

Muslim Launcher respects your privacy:

- **No tracking** - We don't know what apps you use
- **No analytics** - No data sent to servers
- **Local storage** - All data stays on your phone
- **No account required** - No cloud, no login
- **Open source** - Code is auditable

**Permissions:**

- Launcher permission (required to function as launcher)
- Notification access (optional, for prayer reminders)
- Usage stats (optional, for app categorization insights)

---

## 💰 Support

Muslim Launcher is free and open source, built for the Ummah.

If you want to support development:

- [Sociabuzz](https://sociabuzz.com/sadaqahcode/tribe)

Or contribute code, report bugs, spread the word.

---

## 📞 Contact

- 📬 **Email:** sadaqahcode@proton.me
- 💬 **Discord:** https://discord.gg/bPuuyhY5
- 🌐 **Website:** https://flagodna.com
- 🐛 **Issues:** [GitHub](https://github.com/Sadaqahcode/muslim-launcher/issues)

---

## 📜 License

GPL-3.0 - Free to use and modify. See [LICENSE](LICENSE)

---

## 🌟 The Vision

**Today:** A launcher that respects Islamic values  
**Tomorrow:** A complete digital ecosystem where technology serves faith

Muslim Launcher is the first step in building a phone experience that:

- Helps you pray on time
- Reduces phone addiction
- Promotes beneficial activities
- Respects your privacy
- Aligns with Islamic values

**Join us in building the future of Islamic digital life.**

---

## 🌙 About Sadaqah Code

**Sadaqah Code** is an Islamic open-source initiative dedicated to building high-quality, free, and accessible digital tools for Muslims worldwide.

We believe that code can be a form of worship when it serves the community. Our mission is to provide transparent, ad-free, and community-owned software that upholds Islamic values.

> **Note:** Sadaqah Code was founded and is maintained by the **[FlagoDNA](https://flagodna.com)** team. While FlagoDNA leads the technical development, Sadaqah Code represents the spirit of community ownership and open-source contribution.

---

**Bismillah.**

_Sadaqah Code Team_  
_Technology for the Ummah_
