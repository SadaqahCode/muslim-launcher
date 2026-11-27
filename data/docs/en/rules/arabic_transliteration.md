# Arabic Transliteration Rules (Arabic → Latin)

> **About this document**
>
> - This is the **main reference** for transliterating Arabic texts in this repository. When work in this repository disagrees with it, follow this document or propose a change to it.
> - It is **not a recognized standard** and **not a universal rule**. It does not apply outside this repository, and it does not claim that other transliteration systems are wrong.
> - It states **our position as the developers**: the choices we make so that transliterations here stay consistent and open to checking. It carries no religious authority.
> - It can be wrong and it can change. If you find a transliteration that does not follow these rules, or a rule that should be improved, please help us. See [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

These rules define one Latin system for readers from different language backgrounds (for example, in Indonesia, Malaysia, the Arab world, India, Europe, Africa, and the United States). Translation is defined separately in [`arabic_translation.md`](./arabic_translation.md).

## 1. Principles

1. **Single source: vocalized Arabic text** (with harakat), checked against a reliable reference to the original collection (for example, sunnah.com). Record the collection and hadith number.
2. **Transliterate directly from Arabic**, by applying these rules. Do not copy or adapt another transliteration, and do not write from habit or from how a word is usually spelled.
3. **One Arabic consonant = one fixed Latin representation**, either a single symbol or a two-letter combination (such as `th` or `sh`). Do not swap representations.
4. **Write the original letters, not the resulting sound.** Idgham, ikhfa', iqlab, and other tajwid rules are not reflected in the Latin text.
5. **Parentheses `(x)` mark endings** that are pronounced when connected to the next word (wasl) and dropped when stopping (waqf) (see section 5).
6. **Latin is only a bridge.** It does not replace the vocalized Arabic text or learning pronunciation by listening.
7. **Use Unicode NFC** for all Latin text.

## 2. Consonants

| Arabic        | Latin | Note                                                                              |
| ------------- | ----- | --------------------------------------------------------------------------------- |
| ب             | b     |                                                                                   |
| ت             | t     |                                                                                   |
| ث             | th    |                                                                                   |
| ج             | j     |                                                                                   |
| ح             | ḥ     | Pharyngeal h                                                                      |
| خ             | kh    |                                                                                   |
| د             | d     |                                                                                   |
| ذ             | dh    |                                                                                   |
| ر             | r     |                                                                                   |
| ز             | z     |                                                                                   |
| س             | s     |                                                                                   |
| ش             | sh    | See open decisions for two-letter combinations                                    |
| ص             | ṣ     | Emphatic                                                                          |
| ض             | ḍ     | Emphatic                                                                          |
| ط             | ṭ     | Emphatic                                                                          |
| ظ             | ẓ     | Emphatic                                                                          |
| ع             | ʿ     | ʿAyn (U+02BF)                                                                     |
| غ             | gh    |                                                                                   |
| ف             | f     |                                                                                   |
| ق             | q     |                                                                                   |
| ك             | k     |                                                                                   |
| ل             | l     |                                                                                   |
| م             | m     |                                                                                   |
| ن             | n     |                                                                                   |
| ه             | h     |                                                                                   |
| و (consonant) | w     |                                                                                   |
| ي (consonant) | y     |                                                                                   |
| ء             | ʾ     | Hamza (U+02BE). Written mid-word and word-finally; **not written** word-initially |

## 3. Vowels

| Arabic                              | Latin | Note      |
| ----------------------------------- | ----- | --------- |
| ـَ (fatha)                          | a     | Short     |
| ـِ (kasra)                          | i     | Short     |
| ـُ (damma)                          | u     | Short     |
| ـَا, ـَى (long alif / alif maqsura) | ā     | Long      |
| ـِي (long ya)                       | ī     | Long      |
| ـُو (long waw)                      | ū     | Long      |
| ـَيْ                                | ay    | Diphthong |
| ـَوْ                                | aw    | Diphthong |

## 4. Special cases

| Case                         | Rule                                                                                                                  | Example                                  |
| ---------------------------- | --------------------------------------------------------------------------------------------------------------------- | ---------------------------------------- |
| **Shadda (tashdid)**         | Write the consonant twice                                                                                             | Allāhumma, ṣallayta                      |
| **Tanwin**                   | In parentheses                                                                                                        | Muḥammad(un), Muḥammad(in), Muḥammad(an) |
| **Final case vowel (i'rab)** | In parentheses                                                                                                        | Ibrāhīm(a), ʿālamīn(a)                   |
| **Ta marbuta (ة)**           | Read as `a` when stopping and `at` when connected; written `a(t)`                                                     | raḥma(t)                                 |
| **Definite article (ال)**    | `al-` at the start of a sentence; `l-` after a word ending in a vowel. Sun letters are **not** assimilated in writing | al-shams, fī l-ʿālamīn(a)                |
| **Hamzat al-wasl**           | The hamza itself is not written                                                                                       |                                          |
| **The word Allah (الله)**    | Written `Allāh`                                                                                                       |                                          |
| **Spacing**                  | One space between words; words are written separately                                                                 | wa ʿalā                                  |

## 5. Parentheses rule `(x)`

- **Connected to the next word (wasl):** read what is inside the parentheses.
  `Muḥammad(in) wa` → _Muḥammadin wa_
- **Stopping (waqf):** drop what is inside the parentheses.
  `Muḥammad(in)` → _Muḥammad_
- **Known exception:** when stopping on _tanwin fath_ (`an`) on a word that does not end in ta marbuta, the ending is generally read as a long _ā_ instead of being dropped. How to mark this is not decided (see open decisions).
- Parentheses are only for sounds that are lost or changed when stopping. Sounds that are always read are not put in parentheses.
- Readers apply idgham and other tajwid rules themselves. For example, connected `ḥamīd(un) majīd(un)` is read _ḥamīdum majīd_ (idgham with ghunna). The Latin text is still written `ḥamīd(un) majīd(un)`.

**Parentheses in translations** have a different function: they mark helper words that are not in the Arabic (see [`arabic_translation.md`](./arabic_translation.md)). Wherever both are shown to readers, explain the difference.

## 6. Names

These rules apply to names written in transliteration, such as narrator names in context notes.

- Capitalize names. The definite article stays lowercase and unassimilated: _Abū Masʿūd al-Anṣārī_.
- Names are written without case endings and without parentheses.
- **Inside translated text and glossary translations**, names follow [`arabic_translation.md`](./arabic_translation.md) (for example, "Muhammad" and "Ibrahim"). In transliteration of the Arabic text, they follow this document (_Muḥammad_, _Ibrāhīm_).

## 7. Simplified form (optional)

The full form is the **default** and the reference. The simplified form may be offered as an alternative way to display it. It is derived from the full form and is not maintained as a separate transliteration.

| Full form  | Simplified form                 |
| ---------- | ------------------------------- |
| ṣ, ḍ, ṭ, ẓ | s, d, t, z                      |
| ḥ          | h                               |
| ʿ, ʾ       | '                               |
| ā, ī, ū    | aa, ii, uu (see open decisions) |

**Warning:** the simplified form loses distinctions between letters. For example, ص and س both become `s`, ح and ه both become `h`, and ع and ء both become `'`.

## 8. Review

1. Check the vocalized Arabic against the reference for the original collection.
2. Transliterate using the tables and rules above.
3. Have a person who knows Arabic review it.
4. Treat a transliteration as a **draft** until that review is done, and do not present it as reviewed.

To propose a change to a rule or a transliteration, see [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 9. Example: Salawat Ibrahimiyyah (Sahih Muslim 405)

This example is an **excerpt**: the salawat wording only. In the [source](https://sunnah.com/muslim:405), the hadith continues with a short statement about the salam, which is not included here. Commas in the Latin text are added for readability.

**Arabic**

اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ

**Latin (full form)**

> Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā ṣallayta ʿalā āli Ibrāhīm(a), wa bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā bārakta ʿalā āli Ibrāhīm(a) fī l-ʿālamīn(a), innaka ḥamīd(un) majīd(un)

**Latin (simplified form)**

> Allaahumma salli 'alaa Muhammad(in) wa 'alaa aali Muhammad(in), kamaa sallayta 'alaa aali Ibraahiim(a), wa baarik 'alaa Muhammad(in) wa 'alaa aali Muhammad(in), kamaa baarakta 'alaa aali Ibraahiim(a) fii l-'aalamiin(a), innaka hamiid(un) majiid(un)

## 10. Open decisions

- **Stopping on tanwin fath.** `(an)` suggests the whole ending is dropped, but it is generally read as _ā_ when stopping. A separate notation may be needed.
- **Ta marbuta with a case ending.** `a(t)` does not show the vowel or tanwin that follows the _t_ when connected. Whether to write it, for example as `a(tu)` or `a(tin)`, is not decided.
- **Other final short vowels.** Only tanwin and i'rab endings are put in parentheses. Final vowels that are not i'rab, such as in _innaka_ and _bārakta_, are also usually dropped when stopping. Whether to mark them is not decided.
- **Two-letter combinations.** `th`, `kh`, `dh`, `sh`, and `gh` can be confused with two separate consonants in a row (for example, ت followed by ه). Whether to add a separator is not decided.
- **Cases not yet covered:** the definite article and the word _Allah_ after another word or an attached preposition, hamzat al-wasl at the start of an utterance, and dagger alif.
- **Long vowels in the simplified form.** `aa, ii, uu` keeps vowel length; `a, i, u` loses it. The example uses `aa, ii, uu`.
- **Arabic reviewer.** Not yet assigned.
- **Scope for Qur'an text.** Whether these rules apply to Qur'an transliteration is not decided.
