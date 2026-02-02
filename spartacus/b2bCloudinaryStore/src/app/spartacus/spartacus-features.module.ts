import { NgModule } from '@angular/core';
import {
  AnonymousConsentsModule,
  AuthModule,
  CostCenterOccModule,
  ExternalRoutesModule,
  ProductModule,
  ProductOccModule,
  provideFeatureToggles,
  UserModule,
  UserOccModule,
} from '@spartacus/core';
import {
  AnonymousConsentManagementBannerModule,
  AnonymousConsentsDialogModule,
  BannerCarouselModule,
  BannerModule,
  BreadcrumbModule,
  CategoryNavigationModule,
  CmsParagraphModule,
  ConsentManagementModule,
  FooterNavigationModule,
  HamburgerMenuModule,
  HomePageEventModule,
  LinkModule,
  LoginRouteModule,
  LogoutModule,
  MyAccountV2Module,
  MyCouponsModule,
  MyInterestsModule,
  NavigationEventModule,
  NavigationModule,
  NotificationPreferenceModule,
  PageTitleModule,
  PaymentMethodsModule,
  PDFModule,
  ProductCarouselModule,
  ProductDetailsPageModule,
  ProductFacetNavigationModule,
  ProductImagesModule,
  ProductIntroModule,
  ProductListingPageModule,
  ProductListModule,
  ProductPageEventModule,
  ProductReferencesModule,
  ProductSummaryModule,
  ProductTabsModule,
  ScrollToTopModule,
  SearchBoxModule,
  SiteContextSelectorModule,
  SiteThemeSwitcherModule,
  StockNotificationModule,
  TabParagraphContainerModule,
  VideoModule,
} from '@spartacus/storefront';
import { AsmCustomer360FeatureModule } from './features/asm/asm-customer360-feature.module';
import { AsmFeatureModule } from './features/asm/asm-feature.module';
import { CartBaseFeatureModule } from './features/cart/cart-base-feature.module';
import { CartImportExportFeatureModule } from './features/cart/cart-import-export-feature.module';
import { CartQuickOrderFeatureModule } from './features/cart/cart-quick-order-feature.module';
import { CartSavedCartFeatureModule } from './features/cart/cart-saved-cart-feature.module';
import { WishListFeatureModule } from './features/cart/wish-list-feature.module';
import { CheckoutFeatureModule } from './features/checkout/checkout-feature.module';
import { CpqQuoteFeatureModule } from './features/cpq-quote/cpq-quote-feature.module';
import { CustomerTicketingFeatureModule } from './features/customer-ticketing/customer-ticketing-feature.module';
import { DigitalPaymentsFeatureModule } from './features/digital-payments/digital-payments-feature.module';
import { EpdVisualizationFeatureModule } from './features/epd-visualization/epd-visualization-feature.module';
import { EstimatedDeliveryDateFeatureModule } from './features/estimated-delivery-date/estimated-delivery-date-feature.module';
import { OmfFeatureModule } from './features/omf/omf-feature.module';
import { OpfFeatureModule } from './features/opf/opf-feature.module';
import { OppsFeatureModule } from './features/opps/opps-feature.module';
import { OrderFeatureModule } from './features/order/order-feature.module';
import { OrganizationAccountSummaryFeatureModule } from './features/organization/organization-account-summary-feature.module';
import { OrganizationAdministrationFeatureModule } from './features/organization/organization-administration-feature.module';
import { OrganizationOrderApprovalFeatureModule } from './features/organization/organization-order-approval-feature.module';
import { OrganizationUnitOrderFeatureModule } from './features/organization/organization-unit-order-feature.module';
import { OrganizationUserRegistrationFeatureModule } from './features/organization/organization-user-registration-feature.module';
import { PdfinvoicesModuleFeatureModule } from './features/pdf-invoices/pdfinvoices-module-feature.module';
import { PickupInStoreFeatureModule } from './features/pickup-in-store/pickup-in-store-feature.module';
import { ProductConfiguratorFeatureModule } from './features/product-configurator/product-configurator-feature.module';
import { ProductMultiDimensionalListFeatureModule } from './features/product-multi-dimensional/product-multi-dimensional-list-feature.module';
import { ProductMultiDimensionalSelectorFeatureModule } from './features/product-multi-dimensional/product-multi-dimensional-selector-feature.module';
import { ProductBulkPricingFeatureModule } from './features/product/product-bulk-pricing-feature.module';
import { ProductFutureStockFeatureModule } from './features/product/product-future-stock-feature.module';
import { ProductImageZoomFeatureModule } from './features/product/product-image-zoom-feature.module';
import { ProductVariantsFeatureModule } from './features/product/product-variants-feature.module';
import { QualtricsFeatureModule } from './features/qualtrics/qualtrics-feature.module';
import { QuoteFeatureModule } from './features/quote/quote-feature.module';
import { RequestedDeliveryDateModuleFeatureModule } from './features/requested-delivery-date/requested-delivery-date-module-feature.module';
import { SegmentRefsFeatureModule } from './features/segment-refs/segment-refs-feature.module';
import { SmartEditFeatureModule } from './features/smartedit/smart-edit-feature.module';
import { StoreFinderFeatureModule } from './features/storefinder/store-finder-feature.module';
import { PersonalizationFeatureModule } from './features/tracking/personalization-feature.module';
import { TagManagementFeatureModule } from './features/tracking/tag-management-feature.module';
import { UserFeatureModule } from './features/user/user-feature.module';
import { UserAccountModule } from '@spartacus/user/account';

@NgModule({
  declarations: [],
  imports: [
    AuthModule.forRoot(),
    UserAccountModule,
    LogoutModule,
    LoginRouteModule,
    HamburgerMenuModule,
    SiteContextSelectorModule,
    LinkModule,
    BannerModule,
    CmsParagraphModule,
    TabParagraphContainerModule,
    BannerCarouselModule,
    CategoryNavigationModule,
    NavigationModule,
    FooterNavigationModule,
    BreadcrumbModule,
    ScrollToTopModule,
    PageTitleModule,
    VideoModule,
    PDFModule,
    SiteThemeSwitcherModule,
    UserModule,
    UserOccModule,
    PaymentMethodsModule,
    NotificationPreferenceModule,
    MyInterestsModule,
    MyAccountV2Module,
    StockNotificationModule,
    ConsentManagementModule,
    MyCouponsModule,
    AnonymousConsentsModule.forRoot(),
    AnonymousConsentsDialogModule,
    AnonymousConsentManagementBannerModule,
    ProductModule.forRoot(),
    ProductOccModule,
    ProductDetailsPageModule,
    ProductListingPageModule,
    ProductListModule,
    SearchBoxModule,
    ProductFacetNavigationModule,
    ProductTabsModule,
    ProductCarouselModule,
    ProductReferencesModule,
    ProductImagesModule,
    ProductSummaryModule,
    ProductIntroModule,
    CostCenterOccModule,
    NavigationEventModule,
    HomePageEventModule,
    ProductPageEventModule,
    ExternalRoutesModule.forRoot(),
    UserFeatureModule,
    CartBaseFeatureModule,
    CartSavedCartFeatureModule,
    WishListFeatureModule,
    CartQuickOrderFeatureModule,
    CartImportExportFeatureModule,
    OrderFeatureModule,
    CheckoutFeatureModule,
    PersonalizationFeatureModule,
    TagManagementFeatureModule,
    OpfFeatureModule,
    PdfinvoicesModuleFeatureModule,
    RequestedDeliveryDateModuleFeatureModule,
    CustomerTicketingFeatureModule,
    OrganizationUserRegistrationFeatureModule,
    OrganizationAdministrationFeatureModule,
    OrganizationAccountSummaryFeatureModule,
    OrganizationUnitOrderFeatureModule,
    OrganizationOrderApprovalFeatureModule,
    ProductConfiguratorFeatureModule,
    StoreFinderFeatureModule,
    AsmFeatureModule,
    AsmCustomer360FeatureModule,
    CpqQuoteFeatureModule,
    SegmentRefsFeatureModule,
    OmfFeatureModule,
    OppsFeatureModule,
    EpdVisualizationFeatureModule,
    DigitalPaymentsFeatureModule,
    SmartEditFeatureModule,
    EstimatedDeliveryDateFeatureModule,
    QualtricsFeatureModule,
    ProductMultiDimensionalListFeatureModule,
    ProductMultiDimensionalSelectorFeatureModule,
    ProductFutureStockFeatureModule,
    ProductVariantsFeatureModule,
    ProductImageZoomFeatureModule,
    ProductBulkPricingFeatureModule,
    PickupInStoreFeatureModule,
    QuoteFeatureModule,
  ],
  providers: [
    provideFeatureToggles({
      a11yUseProperTextColorForFutureStockAccordion: true,
      a11yPopoverHighContrast: true,
      a11yTabsManualActivation: true,
      a11yAnonymousConsentMessageInDialog: true,
      a11yQuickOrderSearchListKeyboardNavigation: true,
      a11yKeyboardAccessibleZoom: true,
      a11yTruncatedTextUnitLevelOrderHistory: true,
      a11yPreventCartItemsFormRedundantRecreation: true,
      a11yResetFocusAfterNavigating: true,
      a11yStoreFinderLabel: true,
      a11yLinkBtnsToTertiaryBtns: true,
      a11ySelectImprovementsCustomerTicketingCreateSelectbox: true,
      a11yNgSelectAriaLabelDropdownCustomized: true,
      a11yMiniCartFocusOnMobile: true,
      updateConsentGivenInOnChanges: true,
      a11yQuickOrderSearchBoxRefocusOnClose: true,
      a11yKeyboardFocusInSearchBox: true,
      a11yAddPaddingToCarouselPanel: true,
      a11yNavigationButtonsAriaFixes: true,
      a11yFocusOnCardAfterSelecting: true,
      a11ySearchableDropdownFirstElementFocus: true,
      a11yHideConsentButtonWhenBannerVisible: true,
      a11yRepeatingButtonsUniqueLabels: true,
      a11yHighContrastBorders: true,
      a11yRegionAssociatedHeaders: true,
      a11yHamburgerMenuTrapFocus: true,
      a11yScrollToTopPositioning: true,
      enableCarouselCategoryProducts: true,
      enableClaimCustomerCouponWithCodeInRequestBody: true,
      authorizationCodeFlowByDefault: true,
      incrementProcessesCountForMergeCart: true,
      dispatchLoginActionOnlyWhenTokenReceived: true,
    }),
  ],
})
export class SpartacusFeaturesModule {}
