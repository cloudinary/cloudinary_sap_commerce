# AGENTS.md — cloudinary_sap_commerce

## What this is (one line)
Cloudinary's integration for SAP Commerce Cloud (Hybris): a suite of nine Hybris extensions (Java + ImpEx + Spring), plus Backoffice, SmartEdit, OCC, and Spartacus pieces, that offloads product/content image and video storage, transformation, optimization, and CDN delivery to Cloudinary. It installs into a licensed SAP Commerce project — it is **not** an SDK or a package you install from Maven/npm. The Java package root for every extension is `uk.ptr.cloudinary` (originally partner-built, PTR).

## When to use / when NOT to use
- **Use this when:** the target is an **SAP Commerce Cloud (Hybris)** storefront (B2C accelerator, B2B accelerator, or Spartacus composable) that should manage and deliver media through Cloudinary. Extensions get added to `localextensions.xml` and built with the Hybris `ant` toolchain; credentials are set on the `CloudinaryConfig` Backoffice item.
- **Do NOT use this when:** you're building a **custom Java application** that isn't a Hybris project — use [`cloudinary_java`](https://github.com/cloudinary/cloudinary_java) directly (this suite vendors it as jars). For other commerce platforms use the sibling repo below.
- **Siblings:** [`cloudinary_magento2`](https://github.com/cloudinary/cloudinary_magento2) (Magento/Adobe Commerce), [`cloudinary_wordpress`](https://github.com/cloudinary/cloudinary_wordpress) (WordPress/WooCommerce), [`cloudinary_sfcc_site_cartridge`](https://github.com/cloudinary/cloudinary_sfcc_site_cartridge) (Salesforce Commerce Cloud). Agent/no-code path: [Cloudinary MCP servers](https://github.com/cloudinary/mcp-servers).

## Extension dependency graph (verified against extensioninfo.xml)
All nine live under `ext-cloudinary/cloudinary/`. `cloudinarymediacore` is the root for every extension except the two SmartEdit extensions (`cloudinarymediasmartedit`, `cloudinarymediab2bsmartedit`), which require only the platform `smartedit`/`cmsfacades`/`smartedittools` modules, not core.

```
cloudinarymediacore   (requires commerceservices, mediaconversion, acceleratorservices)
├── cloudinarymediafacades        (requires cloudinarymediacore, acceleratorfacades)
│   ├── cloudinarymediaaddon      (B2C storefront AddOn; + acceleratorstorefrontcommons, addonsupport)
│   ├── cloudinarymediab2baddon   (B2B storefront AddOn; + acceleratorstorefrontcommons, addonsupport)
│   ├── cloudinarymediaocc        (OCC REST for Spartacus; + commercewebservices, cmsocc)
│   └── cloudinarymediawebservices(CMS OCC web services; + webservicescommons, cmsocc, cloudinarymediacore)
├── cloudinarymediabackoffice     (Backoffice UI; + backoffice, mediaconversionbackoffice)
├── cloudinarymediasmartedit      (B2C SmartEdit; requires smartedit, cmsfacades — not core)
└── cloudinarymediab2bsmartedit   (B2B SmartEdit; requires smartedittools, smartedit — not core)
```

`cloudinarymediacore` holds the `CloudinaryConfig` item type, the Media/Product/Category attribute enrichment (`cloudinaryURL`, `cloudinaryPublicId`, `cloudinaryResourceType`, `cloudinaryFetchURL`, ...), the cron jobs (`cloudinaryMediaUploadSyncJob`, `cloudinaryMediaTransformationJob`, `cloudinaryMediaTagUpdateJob`), and the vendored Cloudinary Java SDK. The storefront addons hold **no** credentials.

## Install / build
Nothing installs from a registry — these extensions drop into a **licensed** SAP Commerce platform. Two paths:

- **Installer recipes:** `recipes/cloudinary_b2c.zip` / `recipes/cloudinary_b2b.zip` → unzip into `installer/recipes/`, then `./install.sh -r cloudinary_b2c`.
- **Manual:** add the extensions to `localextensions.xml` (reference: `ext-config/b2c/localextensions.xml`, `ext-config/b2b/localextensions.xml`), pointing a `<path dir='.../ext-cloudinary'/>` at the folder. Active B2C set: `cloudinarymediacore`, `cloudinarymediafacades`, `cloudinarymediabackoffice`, `cloudinarymediaaddon`, `cloudinarymediaocc`, `cloudinarymediab2bsmartedit`, `spartacussampledata`. Commented out: `cloudinarymediasmartedit`, `cloudinarymediawebservices`, `yb2bacceleratorstorefront`.

Then the standard Hybris flow (run inside the platform):

```bash
ant clean all           # compile platform + extensions
ant addoninstall -Daddonnames="cloudinarymediaaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant updatesystem        # or a HAC System Update, importing the cloudinary* project data
```

**There is no CI.** No `.github/workflows`, no GitHub Actions (verified: `contents/.github` → 404). Build/test is the Hybris `ant` toolchain (`ant all` / `ant unittests` / `ant integrationtests`), which needs the licensed SAP Commerce platform and can't run in plain CI. Committed build artifacts (`build/libs/*.jar`) are present in the repo. The Spartacus/Angular apps build separately with `ng build`.

## Configuration
Credentials are **not** platform properties. They're stored in the DB on the `CloudinaryConfig` item type (`cloudinarymediacore-items.xml`), set through **Backoffice**:
- `cloudinaryURL` — the connection string `cloudinary://<API_KEY>:<API_SECRET>@<CLOUD_NAME>` (a stored property on `CloudinaryConfig`). Note: `Media.cloudinaryURL` is a *different*, same-named attribute — the per-asset delivery URL, computed by `cloudinaryUrlAttributeHandler` — don't conflate the two.
- `enableCloudinary`, `cloudinaryFolderPath`, `enableCloudinaryGalleryWidget`, `enableCloudinaryVideoPlayer`, plus global transformation/optimization fields.
- Spartacus reads config over OCC; the storefront's endpoint is set with the `cloudinary.config.url` property (`ext-config/b2c/local.properties`), pointing at `.../occ/v2/<base-site>/cloudinary/configuration`.

Keep the `cloudinaryURL` value out of version control.

## Conventions & gotchas
- **Package root `uk.ptr.cloudinary`** across every extension (PTR = the original partner author). OCC controllers live under `uk.ptr.cloudinary.occ`.
- **Cloudinary Java SDK is vendored, not Maven.** `cloudinarymediacore/lib/cloudinary-core-1.29.0.jar` and `cloudinary-http44-1.29.0.jar`. `cloudinarymediacore`, `cloudinarymediafacades`, `cloudinarymediabackoffice`, and `cloudinarymediawebservices` set `usemaven="false"` in `extensioninfo.xml`; the rest omit it (SAP defaults to non-Maven). No extension declares Maven dependencies — the SDK is the vendored jars above. `maven.compiler.release=21` (Java 21) where declared.
- **Committed build artifacts.** `build/libs/*.jar` and generated output are checked in — don't treat them as source, and don't hand-edit them.
- **Version tags are mixed.** Extensions carry tags from 2005.2 (core, addon, B2C SmartEdit) to 2211.37 (B2B SmartEdit); OCC and web services are 2211.25. Practically targets the SAP Commerce 2005–2211 accelerator lineage.
- **Spartacus/Angular pieces look partly scaffolded.** The reusable library under `Cloudinary Libraries/` documents `npm install @your-org/cloudinary-spartacus` — a placeholder scope, not a published package — and the Angular/Spartacus version numbers (`@angular/core ^21.2.6`, `@spartacus ~221121.x`) are non-standard and internally inconsistent with the demo store's README (Angular CLI 19.2.11). Treat the composable-storefront path as immature and verify before relying on it. <!-- [verify] -->
- **License: MIT** (`Copyright (c) 2020 cloudinary-labs`). Keep new files MIT-compatible.

## Canonical docs
- Cloudinary documentation: https://cloudinary.com/documentation
- Transformation & API references: https://cloudinary.com/documentation/cloudinary_references
- Cloudinary Java SDK (vendored dependency): https://github.com/cloudinary/cloudinary_java
- MCP servers (agent/no-code path): https://github.com/cloudinary/mcp-servers

## Commit / PR conventions
- Branch off and PR against `main` (default branch).
- There's no CI gate — verify by building in a real SAP Commerce platform (`ant clean all`, then `ant updatesystem` / a HAC System Update) and exercising the storefront/Backoffice change manually.
- Keep the vendored SDK jar version and each extension's declared version consistent when bumping.
