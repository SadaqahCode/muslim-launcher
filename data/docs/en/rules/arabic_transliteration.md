# Arabic Transliteration Rules (Arabic → Latin)

> **About this document**
>
> - This is the **primary reference** for transliterating Arabic text in this repository. If any work in this repository conflicts with it, follow this document or propose a change to it.
> - This is **not a recognized standard** and **not a universal rule**. These rules do not apply outside this repository, and they do not claim that other transliteration systems are wrong.
> - This document states **our position as developers**: the choices we made so that transliteration here stays consistent and checkable. It carries no religious authority.
> - This document may be wrong and may change. If you find a transliteration that does not follow these rules, or a rule that needs to be fixed, please help us. See [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

These rules define **two layers** of transliteration:

1. **Default transliteration**: a single Latin system for readers from many language backgrounds (for example, in Indonesia, Malaysia, the Arab world, India, Europe, Africa, and the United States). Its content is the same for all languages and it is the source of truth (sections 1 to 6).
2. **Locale transliteration**: a reading form adapted to the spelling of one reader language. It is derived from the default transliteration, and if it is not available, the reader gets the default transliteration (section 7; per-language profiles are in [`transliteration_locales.md`](./transliteration_locales.md)).

Translation is defined separately in [`arabic_translation.md`](./arabic_translation.md).

## 1. Principles

1. **Single source: the fully vowelled Arabic text** (with harakat), checked against a trustworthy reference to the original collection (for example, sunnah.com). Record the collection name and the hadith number.
2. **The default transliteration is written directly from the Arabic**, by applying these rules. Do not copy or adapt other transliterations, and do not write based on habit or on the way a word is commonly spelled. **A locale transliteration is not written from spelling habits, but derived from the default transliteration** in the way defined in section 7.
3. **One Arabic consonant = one fixed Latin representation**, whether a single symbol or a two-letter combination (such as `th` or `sh`). Do not swap representations. This principle applies to the default transliteration. Each locale profile has its own mapping (see [`transliteration_locales.md`](./transliteration_locales.md)).
4. **The default transliteration writes the original letters, not the resulting sounds.** Tajwid rules such as idgham, ikhfa', iqlab, and others are not reflected in the default Latin text. A locale transliteration may resolve some of these sounds, only to the extent defined in section 7.
5. **Parentheses `(x)` mark endings** that are pronounced when joined to the following word (washal) and omitted when stopping (waqaf) (see section 5). Parentheses exist only in the default transliteration. A locale transliteration has already resolved them.
6. **Latin is only a bridge.** Latin does not replace the vowelled Arabic text, nor learning pronunciation by listening.
7. **Use Unicode NFC** for all Latin text.
8. **Locale takes priority, default is the fallback.** If a locale transliteration is available for the reader's language, that is what is displayed. If not, the default transliteration is displayed (see section 7).

## 2. Consonants

| Arabic        | Latin | Notes                                                                                                   |
| ------------- | ----- | ------------------------------------------------------------------------------------------------------- |
| ب             | b     |                                                                                                         |
| ت             | t     |                                                                                                         |
| ث             | th    |                                                                                                         |
| ج             | j     |                                                                                                         |
| ح             | ḥ     | Pharyngeal h                                                                                            |
| خ             | kh    |                                                                                                         |
| د             | d     |                                                                                                         |
| ذ             | dh    |                                                                                                         |
| ر             | r     |                                                                                                         |
| ز             | z     |                                                                                                         |
| س             | s     |                                                                                                         |
| ش             | sh    | See the open decisions regarding two-letter combinations                                                |
| ص             | ṣ     | Emphatic                                                                                                |
| ض             | ḍ     | Emphatic                                                                                                |
| ط             | ṭ     | Emphatic                                                                                                |
| ظ             | ẓ     | Emphatic                                                                                                |
| ع             | ʿ     | ʿAyn (U+02BF)                                                                                           |
| غ             | gh    |                                                                                                         |
| ف             | f     |                                                                                                         |
| ق             | q     |                                                                                                         |
| ك             | k     |                                                                                                         |
| ل             | l     |                                                                                                         |
| م             | m     |                                                                                                         |
| ن             | n     |                                                                                                         |
| ه             | h     |                                                                                                         |
| و (consonant) | w     |                                                                                                         |
| ي (consonant) | y     |                                                                                                         |
| ء             | ʾ     | Hamzah (U+02BE). Written in the middle and at the end of a word; **not written** at the start of a word |

## 3. Vowels

| Arabic                              | Latin | Notes     |
| ----------------------------------- | ----- | --------- |
| ـَ (fathah)                         | a     | Short     |
| ـِ (kasrah)                         | i     | Short     |
| ـُ (dammah)                         | u     | Short     |
| ـَا, ـَى (long alif / alif maqsura) | ā     | Long      |
| ـِي (long ya)                       | ī     | Long      |
| ـُو (long waw)                      | ū     | Long      |
| ـَيْ                                | ay    | Diphthong |
| ـَوْ                                | aw    | Diphthong |

## 4. Special cases

| Case                          | Rule                                                                                                                  | Example                                  |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------------- | ---------------------------------------- |
| **Shadda (tashdid)**          | Write the consonant twice                                                                                             | Allāhumma, ṣallayta                      |
| **Tanwin**                    | In parentheses                                                                                                        | Muḥammad(un), Muḥammad(in), Muḥammad(an) |
| **Case-ending vowel (i'rab)** | In parentheses                                                                                                        | Ibrāhīm(a), ʿālamīn(a)                   |
| **Ta marbuta (ة)**            | Read `a` when stopping and `at` when joined; written `a(t)`                                                           | raḥma(t)                                 |
| **Definite article (ال)**     | `al-` at the start of a sentence; `l-` after a word ending in a vowel. Sun letters are **not** assimilated in writing | al-shams, fī l-ʿālamīn(a)                |
| **Hamzat al-wasl**            | The hamzah itself is not written                                                                                      |                                          |
| **The word Allah (الله)**     | Written `Allāh`                                                                                                       |                                          |
| **Spacing**                   | One space between words; words are written separately                                                                 | wa ʿalā                                  |

The rules in this table apply to the default transliteration. A locale transliteration derives from them according to section 7.

## 5. Parentheses rules `(x)`

- **Joined to the next word (washal):** read what is inside the parentheses.  
  `Muḥammad(in) wa` → _Muḥammadin wa_
- **Stopping (waqaf):** omit what is inside the parentheses.  
  `Muḥammad(in)` → _Muḥammad_
- **Known exception:** when stopping on a _tanwin fathah_ (`an`) on a word that does not end in ta marbuta, the ending is generally read as a long _ā_ rather than omitted. How to mark this has not been decided (see the open decisions).
- Parentheses are only for sounds that are dropped or changed when stopping. Sounds that are always pronounced are not placed in parentheses.
- Readers of the default transliteration apply idgham and the other tajwid rules themselves. For example, the sequence `ḥamīd(un) majīd(un)` is read _ḥamīdum majīd_ (idgham with ghunnah). The default Latin text is still written `ḥamīd(un) majīd(un)`.

**Parentheses in translation** serve a different function: they mark helper words that are not in the Arabic (see [`arabic_translation.md`](./arabic_translation.md)). Wherever both are shown to the reader, explain the difference. A locale transliteration contains no `(x)` parentheses at all, so that difference only needs to be explained when the default transliteration is displayed.

## 6. Names

These rules apply to names written in transliteration, such as narrator names in context notes.

- Capitalize names. The definite article stays lowercase and is not assimilated: _Abū Masʿūd al-Anṣārī_.
- Names are written without case endings and without parentheses.
- **Inside translated text and glossary translations**, names follow [`arabic_translation.md`](./arabic_translation.md) (for example, "Muhammad" and "Ibrahim"). In the transliteration of Arabic text, names follow this document (_Muḥammad_, _Ibrāhīm_).
- In a locale transliteration, names follow the character mapping of the locale profile and remain capitalized.

## 7. Default and locale layers

### 7.1 Definitions

| Layer                       | Content                                                                              | Who writes it                                                                       |
| --------------------------- | ------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------- |
| **Default transliteration** | One text per dhikr or hadith, following sections 1 to 6. The same for all languages. | Written directly from the Arabic text, then reviewed.                               |
| **Locale transliteration**  | One text per reader language, may be empty. Contains no `(x)` parentheses.           | Derived from the default transliteration (7.3). Exception: non-Latin scripts (7.4). |

The default transliteration is the **source of truth**. A locale transliteration is only a view that can be regenerated from the default at any time.

### 7.2 Order of use: locale takes priority, default is the fallback

When an app or API displays a transliteration to a reader:

1. **If a locale transliteration is available for the reader's language, display it.** Locale sits above default.
2. **If it is not available, display the default transliteration.**

Additional provisions:

- **"Not available"** means the value is `null`, the key is absent, or the string is empty or contains only whitespace.
- **One whole layer, never mixed.** The entire text is taken from the locale or entirely from the default. Do not combine per word or per sentence.
- **Fallback applies per field.** A translation can be available while the locale transliteration is empty. In that case the translation is still displayed and the transliteration falls back to the default.
- **No fallback between locales.** If the `ms` locale is empty, go straight to the default, not to `id`. Each locale's spelling is specific and must not be borrowed as is.
- **Matching uses the base language code** (`id`, `en`, `ms`). Regional variants (for example `en-GB`) follow their base code.
- An app may offer a button to view the default transliteration explicitly, for example for learners who want to see the original letters. That is the reader's choice, not a replacement for the rules above.

### 7.3 How to derive a locale transliteration (Latin script)

A Latin-script locale transliteration is generated from the default transliteration by code, **not written by hand**. If the default transliteration changes, the locale transliteration is regenerated. Do not edit the result directly. Fixes are made in the default transliteration, in the profile mapping, or in the exception list.

The steps, **in order**:

1. **Resolve the parentheses `(x)`.** A stopping point is a comma, a period, or the end of the text in the default transliteration.
   - Parenthesized content directly before a stopping point is **removed** (waqaf).
   - Parenthesized content elsewhere is **opened**, that is, written without the parentheses (washal).
   - `Muḥammad(in) wa ʿalā āli Muḥammad(in),` → _Muḥammadin wa ʿalā āli Muḥammad,_
2. **Resolve the definite article** when the following letter is a sun letter (shamsiyyah), namely `t`, `th`, `d`, `dh`, `r`, `z`, `s`, `sh`, `ṣ`, `ḍ`, `ṭ`, `ẓ`, `l`, `n`.
   - After a word ending in a vowel: `l-` is assimilated. Write the sun letter twice with a hyphen between them, and join it to the end of the previous word.  
     `yaghfir(u) l-dhunūb(a)` → _yaghfirudh-dhunūba_
   - At the start of an utterance: `al-` becomes `a` + letter + `-` + letter.  
     `al-shams` → _ash-shams_
   - Moon letters (qamariyyah) do not change: `fī l-ʿālamīn(a)` keeps `l-`.
3. **Apply the word exception list** (a locale profile may have one). Example: the alif in _anā_ is only pronounced when stopping, so a profile may write it without the long alif when joined.
4. **Map the characters** according to the locale profile table (see [`transliteration_locales.md`](./transliteration_locales.md)).
5. **Normalize to Unicode NFC.**

Things the steps above do **not** apply (see the open decisions):

- Other tajwid rules, such as idgham of tanwin (`ḥamīd(un) majīd(un)` read as _ḥamīdum majīd_).
- The exception for stopping on tanwin fathah (read as _ā_).
- Separating or joining prefixes such as `fa`, `bi`, and `wa`: prefixes written separately in the default transliteration remain separate in the locale transliteration.

### 7.4 Non-Latin-script locales

Transliteration into other scripts (for example Hindi, Thai, or Urdu) is **not derived from the Latin text** by code. It is written directly from the vowelled Arabic text by a native speaker, then reviewed by an Arabic reviewer. Until one exists, rule 7.2 applies: the reader gets the default transliteration.

### 7.5 Display

- An app should indicate which form is being shown (for example "Indonesian spelling" or "standard transliteration"), so the reader knows whether the text they are looking at writes letters or sounds.
- **Warning:** a locale transliteration removes distinctions between letters. For example, in a profile that does not distinguish emphatic letters, ص and س both become `s`, ح and ه both become `h`, and ع and ء both become `'`. For this reason a locale transliteration must not be used to check the original letters. Use the Arabic text or the default transliteration for that.

## 8. Locale profiles

Per-language profiles (character mapping table, word exception list, and examples) are in a separate file: **[`transliteration_locales.md`](./transliteration_locales.md)**. Read that file before creating or reviewing a locale transliteration.

This document only defines the general rules (section 7). A language that does not have a profile in that file has no locale transliteration, so the reader gets the default transliteration (section 7.2).

## 9. Review

1. Check the vowelled Arabic text against a reference to the original collection.
2. Transliterate using the tables and rules above (default transliteration).
3. Ask someone who understands Arabic to review it.
4. Treat the transliteration as a **draft** until that review is complete, and do not present it as reviewed.
5. A Latin-script locale transliteration **inherits the status** of the default transliteration it is derived from. If the default is still a draft, the locale is also a draft. If the default changes, regenerate the locale.

To propose changes to the rules or to a transliteration, see [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 10. Example: Salawat Ibrahimiyyah (Sahih Muslim 405)

This example is an **excerpt**: only the salawat wording. In the [source](https://sunnah.com/muslim:405), the hadith continues with a short statement about salam, which is not included here. The commas in the Latin text are added for readability, and those commas also serve as stopping points in step 1 of section 7.3. Examples of locale transliteration are in [`transliteration_locales.md`](./transliteration_locales.md).

**Arabic**

اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ

**Default transliteration**

> Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā ṣallayta ʿalā āli Ibrāhīm(a), wa bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā bārakta ʿalā āli Ibrāhīm(a) fī l-ʿālamīn(a), innaka ḥamīd(un) majīd(un)

## 11. Open decisions

- **Stopping on tanwin fathah.** `(an)` implies that the whole ending is dropped, but it is generally read as _ā_ when stopping. A separate notation may be needed.
- **Ta marbuta with a case ending.** `a(t)` does not show the vowel or tanwin that follows _t_ when joined. Whether it should be written, for example, as `a(tu)` or `a(tin)`, has not been decided.
- **Other final short vowels.** Only tanwin and i'rab endings are placed in parentheses. Final vowels that are not i'rab, as in _innaka_ and _bārakta_, are also usually dropped when stopping. Whether to mark them has not been decided.
- **Two-letter combinations.** `th`, `kh`, `dh`, `sh`, and `gh` can be misread as two separate consecutive consonants (for example, ت followed by ه). Whether to add a separator has not been decided.
- **Cases not yet covered:** the definite article and the word _Allah_ after another word or an attached preposition, hamzat al-wasl at the start of an utterance, and the dagger alif (alif khanjariyah).
- **Stopping points in locale transliteration.** Currently commas, periods, and the end of the text in the default transliteration are treated as stopping points. Yet the commas in the default text are added for readability. Whether commas should be managed strictly as part of the data has not been decided.
- **Other tajwid rules in locale transliteration**, for example idgham of tanwin (_ḥamīdum majīd_), are not yet applied.
- **Writing of the assimilated article.** Currently sun letters are joined to the previous word (_yaghfirudh-dhunūba_), while moon letters stay separate (`l-`). Whether the two should be made uniform has not been decided.
- **Prefixes such as `fa`, `bi`, and `wa`.** In locale transliteration they currently stay separate. Whether readers would be better served by a joined form (for example _faghfir_) has not been decided.
- **Where to store the word exception list** (for example _anā_ becoming _ana_): in code, in a profile file, or elsewhere.
- **Fallback for non-Latin-script locales.** Readers who cannot read Latin script may not be helped by the default transliteration. Whether the app may display the Arabic text alone in that case has not been decided.
- **Arabic reviewer.** Not yet appointed.
- **Scope for Quranic text.** Whether these rules apply to the transliteration of the Quran has not been decided.
