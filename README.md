# Cloudinary SAP Commerce Cloud integration

[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](./LICENSE)

A Cloudinary media-management and delivery integration for SAP Commerce Cloud (Hybris), shipped as a suite of Hybris extensions (Java + ImpEx + Spring) with Backoffice, SmartEdit, OCC, and Spartacus pieces. It offloads product and content image and video storage, transformation, optimization, and CDN delivery to Cloudinary across the B2C and B2B accelerator storefronts and the composable (Spartacus) storefront. The extensions target the SAP Commerce 2005–2211 accelerator lineage (extension version tags range from 2005.2 to 2211.37), build against Java 21, and bundle the Cloudinary Java SDK 1.29.0 as vendored jars in `cloudinarymediacore/lib/`. It installs into a licensed SAP Commerce project — it isn't a standalone application or a Maven/npm package.

## Extensions

The nine Cloudinary Hybris extensions live under `ext-cloudinary/cloudinary/`. `cloudinarymediacore` is the base; facades sit on core; the storefront addons, OCC, and web services sit on facades; Backoffice sits on core. The two SmartEdit extensions are the exception — they sit on the platform `smartedit`/`cmsfacades` modules, not on core. Every extension except the SmartEdit pair transitively requires core.

| Extension | Purpose | Requires |
|---|---|---|
| `cloudinarymediacore` | Base services extension: defines the `CloudinaryConfig` item type (credentials + global transformation/optimization settings), enriches Media/Product/Category with Cloudinary attributes, ships the upload-sync / transformation / tag-update cron jobs, and vendors the Cloudinary Java SDK jars. | `commerceservices`, `mediaconversion`, `acceleratorservices` |
| `cloudinarymediafacades` | Facade layer over core — exposes Cloudinary config and media data to storefront and OCC controllers. | `cloudinarymediacore`, `acceleratorfacades` |
| `cloudinarymediaaddon` | B2C accelerator storefront AddOn: Cloudinary gallery, video, product carousel, variant selector, and responsive product-image JSP tags/CMS components. Holds no credentials. | `cloudinarymediafacades`, `acceleratorstorefrontcommons`, `addonsupport` |
| `cloudinarymediab2baddon` | B2B equivalent of the addon for the `yb2bacceleratorstorefront`. | `cloudinarymediafacades`, `acceleratorstorefrontcommons`, `addonsupport` |
| `cloudinarymediabackoffice` | Backoffice integration — the Cloudinary upload widget / media selector and the admin UI where merchandisers manage Cloudinary media and the `CloudinaryConfig` settings. | `backoffice`, `cloudinarymediacore`, `mediaconversionbackoffice` |
| `cloudinarymediasmartedit` | SmartEdit in-context editing for B2C — a Cloudinary media container field / media selector. | `smartedit`, `cmsfacades` |
| `cloudinarymediab2bsmartedit` | SmartEdit media file selector app for B2B (Angular/TS). Version tag 2211.37 (newest). | `smartedittools`, `smartedit` |
| `cloudinarymediaocc` | OCC REST layer for headless/Spartacus — exposes Cloudinary config (cloud name, gallery config, flags) to the composable storefront. | `commercewebservices`, `cloudinarymediafacades`, `cmsocc` |
| `cloudinarymediawebservices` | CMS OCC web services around Cloudinary media and config. | `webservicescommons`, `cloudinarymediafacades`, `cmsocc`, `cloudinarymediacore` |

The repo also ships `spartacussampledata` (demo sample data) and a customized `yb2bacceleratorstorefront` at the repo root for the B2B demo, plus Spartacus demo stores and a reusable Angular library under `Cloudinary Libraries/` and `spartacus/`.

## Installation

These extensions install into an existing, licensed SAP Commerce platform — there's no package to download from a registry. Two paths:

**Installer recipes.** The repo ships SAP Commerce Cloud installer recipes as `recipes/cloudinary_b2c.zip` and `recipes/cloudinary_b2b.zip`. Unzip the one you want into your platform's `installer/recipes/`, then run the installer from the platform root:

```bash
./install.sh -r cloudinary_b2c
```

**Manual wiring.** Add the extensions to your `localextensions.xml`, using `ext-config/b2c/localextensions.xml` (or `ext-config/b2b/localextensions.xml`) as the reference list. Point a `<path dir='.../ext-cloudinary'/>` at the extensions folder, then declare each extension. The active B2C set is:

```xml
<extension name='cloudinarymediacore' />
<extension name='cloudinarymediafacades' />
<extension name='cloudinarymediabackoffice' />
<extension name='cloudinarymediaaddon' />
<extension name='cloudinarymediaocc' />
<extension name='cloudinarymediab2bsmartedit' />
```

`cloudinarymediasmartedit` and `cloudinarymediawebservices` ship commented out in the reference list — enable them if you need B2C SmartEdit or the CMS OCC web services.

Then build and initialize the platform:

```bash
ant clean all           # compile the platform with the Cloudinary extensions
ant addoninstall -Daddonnames="cloudinarymediaaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
ant updatesystem        # or run a System Update in the HAC, importing the cloudinary* project data
```

`ant addoninstall` wires the storefront AddOn into your accelerator storefront (use `cloudinarymediab2baddon` and your B2B storefront for B2B). The ImpEx sample data for each addon lives under its `resources/.../import`. The Spartacus/Angular apps under `spartacus/` build separately with `ng build`.

## Configuration

Cloudinary credentials aren't platform properties — they're stored in the database on the `CloudinaryConfig` item type (defined in `cloudinarymediacore-items.xml`) and set through Backoffice. The credential field is `cloudinaryURL`, which takes your Cloudinary connection string in the `cloudinary://<API_KEY>:<API_SECRET>@<CLOUD_NAME>` format (the `CLOUDINARY_URL` value from the [Cloudinary Console](https://console.cloudinary.com/)).

Related fields on the same item include `enableCloudinary`, `cloudinaryFolderPath`, `enableCloudinaryGalleryWidget`, and `enableCloudinaryVideoPlayer`, plus the global transformation/optimization settings. Because the credential lives in the database, keep it out of version control.

For the composable (Spartacus) storefront, the OCC endpoint the storefront reads config from is set with the `cloudinary.config.url` property (in `ext-config/b2c/local.properties` it points at `.../occ/v2/<base-site>/cloudinary/configuration`).

## For AI agents

This repo is Cloudinary's integration for SAP Commerce Cloud (Hybris) — a suite of Java/ImpEx/Spring extensions installed into a licensed SAP Commerce project, not a general-purpose SDK. For a different platform or a direct-code path, route to the right repo:

| Target platform | Repo |
|---|---|
| Magento 2 / Adobe Commerce | [`cloudinary_magento2`](https://github.com/cloudinary/cloudinary_magento2) |
| WordPress / WooCommerce | [`cloudinary_wordpress`](https://github.com/cloudinary/cloudinary_wordpress) |
| Salesforce Commerce Cloud | [`cloudinary_sfcc_site_cartridge`](https://github.com/cloudinary/cloudinary_sfcc_site_cartridge) |
| A custom Java application | [`cloudinary_java`](https://github.com/cloudinary/cloudinary_java) — the SDK this suite vendors |
| Cloudinary operations as agent tools | [Cloudinary MCP servers](https://github.com/cloudinary/mcp-servers) |

## Links

- [Repository](https://github.com/cloudinary/cloudinary_sap_commerce)
- [Cloudinary documentation](https://cloudinary.com/documentation)
- [Transformation and API references](https://cloudinary.com/documentation/cloudinary_references)
- [Documentation llms.txt index](https://cloudinary.com/documentation/llms.txt)

Released under the MIT license.
