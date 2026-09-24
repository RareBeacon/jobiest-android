# Architecture Decision Record (ADR-001): Language & UI Framework Selection

## Context
Jobiest is a multi-sided AI career platform designed to help job seekers find, target, tailor documents for, and track applications across global job boards (Greenhouse, Lever, Ashby, Workable, SmartRecruiters) and automated browser submissions.

The requirement is to create a genuine, production-grade native Android client communicating with the existing Jobiest backend at `https://www.jobiest.ai/api/` and Supabase Auth.

## Evaluated Options
1. **Java + Traditional XML Layouts**
2. **Kotlin + Traditional XML Layouts**
3. **Kotlin + Jetpack Compose (Selected)**

---

## Detailed Comparison

| Criteria | Java + XML Layouts | Kotlin + Jetpack Compose |
| :--- | :--- | :--- |
| **Official Android Status** | Maintenance / Legacy support | **Official First-Class Default** (Android Developers 2024+) |
| **UI Paradigm** | Imperative, verbose view hierarchy, `findViewById`/ViewBinding | **Declarative**, unidirectional data flow, reactive state rendering |
| **State Management** | Manual listeners, boilerplate mutations | **First-class StateFlow / Compose State (`mutableStateOf`)** |
| **Null Safety** | Runtime `NullPointerException` risks | **Compile-time Null Safety**, eliminating common crash vectors |
| **Concurrency** | Threads, Runnables, Callbacks, RxJava | **Kotlin Coroutines & Flow**, structured lightweight concurrency |
| **Network & Serialization** | Gson/Jackson reflection | **Kotlinx Serialization**, zero-reflection, high performance |
| **Code Footprint** | ~2.5x larger boilerplate | **Concise, idiomatic, and maintainable** |
| **Device Adaptation** | Separate XML files for screens/orientations | Dynamic responsive layouts using standard Kotlin logic |
| **Security Handling** | Prone to object leakage | Direct integration with `androidx.security.crypto` |

---

## Architectural Decision
**We explicitly select Kotlin + Jetpack Compose (Material 3).**

### Rationale:
1. **Google & Android Official Recommendation**: Since 2019, Android has been Kotlin-first. In 2024+, Jetpack Compose is the official standard for all new native Android user interfaces.
2. **Performance & Reliability**: Eliminating XML view inflation reduces memory overhead and UI jank. Kotlin's strong type system and null safety prevent common runtime crashes on candidate devices.
3. **Reactive Command Center Alignment**: Jobiest's dynamic application pipeline and real-time quotas require declarative UI updates that map directly to Kotlin Coroutines and `StateFlow`.
4. **Clean Architecture Separation**: Kotlin facilitates clean separation between Presentation (Compose + ViewModel), Domain (UseCases + Models), Data (Repositories), and Network/Security layers.
