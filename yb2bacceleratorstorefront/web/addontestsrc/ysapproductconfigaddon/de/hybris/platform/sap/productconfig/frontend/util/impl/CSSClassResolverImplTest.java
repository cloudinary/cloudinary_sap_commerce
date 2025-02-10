/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


@UnitTest
public class CSSClassResolverImplTest extends CSSClassResolverImplTestBase
{

	@Test
	public void testGetValueStyleClass_noError()
	{
		final CsticData cstic = new CsticData();
		cstic.setCsticStatus(CsticStatusType.DEFAULT);
		final String inputStyle = classUnderTest.getValueStyleClass(cstic);

		assertContainsStyleClass(inputStyle, CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE);

	}

	@Test
	public void testGetValueStyleClass_error()
	{
		final CsticData cstic = new CsticData();
		cstic.setCsticStatus(CsticStatusType.ERROR);

		final String inputStyle = classUnderTest.getValueStyleClass(cstic);

		assertContainsStyleClass(inputStyle, CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_ERROR,
				CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE);
	}
	
	@Test
	public void testGetValueStyleClass_errorWithMultiUIElement()
	{
		final CsticData cstic = new CsticData();
		cstic.setCsticStatus(CsticStatusType.ERROR);
		cstic.setType(UiType.CHECK_BOX_LIST);

		final String inputStyle = classUnderTest.getValueStyleClass(cstic);

		assertContainsStyleClass(inputStyle, CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_ERROR,
				CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE, CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_MULTI_LIST);
	}	

	@Test
	public void testGetLabelStyle_StatusDefault()
	{
		final CsticData cstic = new CsticData();
		cstic.setRequired(false);
		cstic.setCsticStatus(CsticStatusType.DEFAULT);

		final String labelStyle = classUnderTest.getLabelStyleClass(cstic);

		assertContainsStyleClass(labelStyle, CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL);

	}

	@Test
	public void testGetLabelStyle_StatusConflict()
	{
		final CsticData cstic = new CsticData();
		cstic.setRequired(false);
		cstic.setCsticStatus(CsticStatusType.CONFLICT);

		final String labelStyle = classUnderTest.getLabelStyleClass(cstic);
		assertContainsStyleClass(labelStyle, CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL, CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL_CONFLICT);
	}
	
	
	@Test
	public void testGetLabelStyle_StatusSucces()
	{
		final CsticData cstic = new CsticData();
		cstic.setRequired(false);
		cstic.setCsticStatus(CsticStatusType.FINISHED);

		final String labelStyle = classUnderTest.getLabelStyleClass(cstic);

		assertContainsStyleClass(labelStyle, CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL,
				CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL_SUCCESS);
	}

	@Test
	public void testGetLabelStyle_StatusWarning()
	{
		final CsticData cstic = new CsticData();
		cstic.setRequired(false);
		cstic.setCsticStatus(CsticStatusType.WARNING);

		final String labelStyle = classUnderTest.getLabelStyleClass(cstic);

		assertContainsStyleClass(labelStyle, CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL,
				CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL_WARNING);
	}

	@Test
	public void testGetLabelStyle_notRequiredError()
	{
		final CsticData cstic = new CsticData();
		cstic.setRequired(false);
		cstic.setCsticStatus(CsticStatusType.ERROR);

		final String labelStyle = classUnderTest.getLabelStyleClass(cstic);
		assertContainsStyleClass(labelStyle, CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL,
				CSSClassResolverImpl.STYLE_CLASS_CSTIC_LABEL_ERROR);
	}

	@Test
	public void testGetValuePromoStyleDefault()
	{
		final CsticData cstic = new CsticData();
		cstic.setRequired(false);
		cstic.setCsticStatus(CsticStatusType.DEFAULT);

		String style = classUnderTest.getValuePromoStyleClass(cstic, null);
		assertTrue(style.isEmpty());

		final CsticValueData value = new CsticValueData();
		style = classUnderTest.getValuePromoStyleClass(cstic, value);
		assertTrue(style.isEmpty());


		final List<ProductConfigMessageData> uiMessages = new ArrayList(0);
		value.setMessages(uiMessages);
		style = classUnderTest.getValuePromoStyleClass(cstic, value);
		assertTrue(style.isEmpty());


		final ProductConfigMessageData uiMessage = new ProductConfigMessageData();
		uiMessage.setMessage("message with out type");

		style = classUnderTest.getValuePromoStyleClass(cstic, value);
		assertTrue(style.isEmpty());


	}

	@Test
	public void testGetValuePromoStyleWithoutPromo()
	{
		final CsticData cstic = createCsticWithValueAndPromoMessage("message", ProductConfigMessagePromoType.PROMO_OPPORTUNITY);
		final CsticValueData value = cstic.getDomainvalues().get(0);
		value.getMessages().get(0).setPromoType(null);

		assertEquals("", classUnderTest.getValuePromoStyleClass(cstic, value));

	}

	@Test
	public void testGetValuePromoStyleOpportunity()
	{
		final CsticData cstic = createCsticWithValueAndPromoMessage("message", ProductConfigMessagePromoType.PROMO_OPPORTUNITY);

		final String style = classUnderTest.getValuePromoStyleClass(cstic, cstic.getDomainvalues().get(0));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY);
	}


	@Test
	public void testGetValuePromoStyleApplied()
	{
		final CsticData cstic = createCsticWithValueAndPromoMessage("message", ProductConfigMessagePromoType.PROMO_APPLIED);

		final String style = classUnderTest.getValuePromoStyleClass(cstic, cstic.getDomainvalues().get(0));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED);
	}

	@Test
	public void testGetGroupStyle_Conflict()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.CONFLICT);
		group.setCollapsed(false);
		final String groupStyle = classUnderTest.getGroupStyleClass(group);

		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP, CSSClassResolverImpl.STYLE_CLASS_GROUP_OPEN,
				CSSClassResolverImpl.STYLE_CLASS_GROUP_CONFLICT);
	}

	@Test
	public void test_MenuNodeStyleClass_Leaf()
	{
		final UiGroupData group = createUiGroupWithNoSubGroup();
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(2));
		assertEquals(style, 2, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "2",
				CSSClassResolverImpl.STYLE_CLASS_MENU_LEAF);
	}

	@Test
	public void test_MenuNodeStyleClass_Node()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 3, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_EXPANDED);
	}

	@Test
	public void test_MenuNodeStyleClass_NonConfLeaf()
	{
		final UiGroupData group = createUiGroupWithNoSubGroup();
		group.setConfigurable(false);
		group.setGroupType(GroupType.INSTANCE);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 2, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NON_CONF_LEAF);
	}

	@Test
	public void test_MenuNodeStyleClass_Node_Error()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		group.setGroupStatus(GroupStatusType.ERROR);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 4, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_EXPANDED,
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_ERROR);
	}

	@Test
	public void test_MenuNodeStyleClass_Node_Conflict()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		group.setGroupStatus(GroupStatusType.CONFLICT);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 4, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_EXPANDED,
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_CONFLICT);
	}

	
	@Test
	public void test_MenuNodeStyleClass_Node_Warning()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		group.setGroupStatus(GroupStatusType.WARNING);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 4, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_EXPANDED,
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_WARNING);
	}

	@Test
	public void test_MenuNodeStyleClass_Node_Ok()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		group.setGroupStatus(GroupStatusType.FINISHED);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 4, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_EXPANDED,
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_COMPLETED);
	}

	@Test
	public void test_MenuNodeStyleClass_Node_Collapsed()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		group.setCollapsedInSpecificationTree(true);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 3, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_COLLAPSED);
	}

	@Test
	public void test_MenuNodeStyleClass_NonConfNode_Collapsed()
	{
		final UiGroupData group = createUiGroupWithSubGroup();
		group.setCollapsedInSpecificationTree(true);
		group.setConfigurable(false);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(1));
		assertEquals(style, 3, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "1",
				CSSClassResolverImpl.STYLE_CLASS_MENU_NODE, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_COLLAPSED);
	}

	@Test
	public void test_MenuNodeStyleClass_Leaf_Error()
	{
		final UiGroupData group = createUiGroupWithNoSubGroup();
		group.setGroupStatus(GroupStatusType.ERROR);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(2));
		assertEquals(style, 3, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "2",
				CSSClassResolverImpl.STYLE_CLASS_MENU_LEAF, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_ERROR);
	}

	@Test
	public void test_MenuNodeStyleClass_Leaf_Warning()
	{
		final UiGroupData group = createUiGroupWithNoSubGroup();
		group.setGroupStatus(GroupStatusType.WARNING);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(2));
		assertEquals(style, 3, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "2",
				CSSClassResolverImpl.STYLE_CLASS_MENU_LEAF, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_WARNING);
	}

	@Test
	public void test_MenuNodeStyleClass_Leaf_Ok()
	{
		final UiGroupData group = createUiGroupWithNoSubGroup();
		group.setGroupStatus(GroupStatusType.FINISHED);
		final String style = classUnderTest.getMenuNodeStyleClass(group, Integer.valueOf(2));
		assertEquals(style, 3, getNumberOfStyleClasses(style));
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_CLASS_MENU_LEVEL + "2",
				CSSClassResolverImpl.STYLE_CLASS_MENU_LEAF, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_COMPLETED);
	}

	@Test
	public void testGetGroupStyle_ConflicGroup()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.CONFLICT);
		group.setCollapsed(true);
		group.setGroupType(GroupType.CONFLICT);
		final String groupStyle = classUnderTest.getGroupStyleClass(group, true);
		assertEquals(2, getNumberOfStyleClasses(groupStyle));
		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP,
				CSSClassResolverImpl.STYLE_CLASS_CONFLICTGROUP);

	}

	@Test
	public void testGetGroupStyle_Error()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.ERROR);
		group.setCollapsed(true);

		final String groupStyle = classUnderTest.getGroupStyleClass(group);

		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP, CSSClassResolverImpl.STYLE_CLASS_GROUP_CLOSE,
				CSSClassResolverImpl.STYLE_CLASS_GROUP_ERROR);
	}

	@Test
	public void testGetGroupStyle_Warning()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.WARNING);
		group.setCollapsed(false);
		final String groupStyle = classUnderTest.getGroupStyleClass(group);

		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP, CSSClassResolverImpl.STYLE_CLASS_GROUP_OPEN,
				CSSClassResolverImpl.STYLE_CLASS_GROUP_WARNING);
	}

	@Test
	public void testGetGroupStyle_Default()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.DEFAULT);
		group.setCollapsed(true);
		final String groupStyle = classUnderTest.getGroupStyleClass(group);

		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP, CSSClassResolverImpl.STYLE_CLASS_GROUP_CLOSE);

	}

	@Test
	public void testGetGroupStyle_hideExpandCollapse()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.DEFAULT);
		group.setCollapsed(true);
		final String groupStyle = classUnderTest.getGroupStyleClass(group, true);
		assertEquals(1, getNumberOfStyleClasses(groupStyle));
		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP);

	}

	@Test
	public void testGetGroupStyle_Finished()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.FINISHED);
		group.setCollapsed(false);
		final String groupStyle = classUnderTest.getGroupStyleClass(group);

		assertContainsStyleClass(groupStyle, CSSClassResolverImpl.STYLE_CLASS_GROUP, CSSClassResolverImpl.STYLE_CLASS_GROUP_OPEN,
				CSSClassResolverImpl.STYLE_CLASS_GROUP_FINISHED);
	}

	@Test
	public void testGetGroupStatusTooltipKey()
	{
		final UiGroupData group = new UiGroupData();
		group.setGroupStatus(GroupStatusType.ERROR);
		String tooltip = classUnderTest.getGroupStatusTooltipKey(group);
		assertEquals(CSSClassResolverImpl.RESOURCE_KEY_GROUP_ERROR_TOOLTIP, tooltip);
		group.setGroupStatus(GroupStatusType.FINISHED);
		tooltip = classUnderTest.getGroupStatusTooltipKey(group);
		assertEquals(CSSClassResolverImpl.RESOURCE_KEY_GROUP_FINISHED_TOOLTIP, tooltip);
		group.setGroupStatus(GroupStatusType.WARNING);
		tooltip = classUnderTest.getGroupStatusTooltipKey(group);
		assertEquals(CSSClassResolverImpl.RESOURCE_KEY_GROUP_ERROR_TOOLTIP, tooltip);
		group.setGroupStatus(GroupStatusType.DEFAULT);
		tooltip = classUnderTest.getGroupStatusTooltipKey(group);
		assertEquals("", tooltip);

	}

	@Test
	public void testGetMessageTextAdditionalStyleClass()
	{
		final ProductConfigMessageData promoMessage = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		final String additionalStylePromo = classUnderTest.getMessageTextAdditionalStyleClass(promoMessage);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED, additionalStylePromo);
		final ProductConfigMessageData opptMessage = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY,
				null);
		final String additionalStyleOppt = classUnderTest.getMessageTextAdditionalStyleClass(opptMessage);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY, additionalStyleOppt);
		final ProductConfigMessageData simpleMessageInfo = createMessage("Message 3", null, ProductConfigMessageUISeverity.INFO);
		final String additionalStyleSimple = classUnderTest.getMessageTextAdditionalStyleClass(simpleMessageInfo);
		assertEquals(StringUtils.EMPTY, additionalStyleSimple);
	}

	@Test
	public void testGetExtendedMessageStyleClass()
	{
		final ProductConfigMessageData promoMessage = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		final String extendedStylePromo = classUnderTest.getExtendedMessageStyleClass(promoMessage);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_APPLIED_EXTENDED, extendedStylePromo);
		final ProductConfigMessageData opptMessage = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY,
				null);
		final String extendedStyleOppt = classUnderTest.getExtendedMessageStyleClass(opptMessage);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_PROMO_OPPOTUNITY_EXTENDED, extendedStyleOppt);
		final ProductConfigMessageData simpleMessageInfo = createMessage("Message 3", null, ProductConfigMessageUISeverity.INFO);
		final String extendedStyleSimple = classUnderTest.getExtendedMessageStyleClass(simpleMessageInfo);
		assertEquals(StringUtils.EMPTY, extendedStyleSimple);
	}

	@Test
	public void testGetMessageIconStyleClass()
	{
		final ProductConfigMessageData promoMessage = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		final String messageStylePromo = classUnderTest.getMessageIconStyleClass(promoMessage);
		assertEquals(StringUtils.EMPTY, messageStylePromo);
		final ProductConfigMessageData opptMessage = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY,
				null);
		final String messageStyleOppt = classUnderTest.getMessageIconStyleClass(opptMessage);
		assertEquals(StringUtils.EMPTY, messageStyleOppt);
		final ProductConfigMessageData simpleMessageInfo = createMessage("Message 3", null, ProductConfigMessageUISeverity.INFO);
		final String messageStyleSimpleInfo = classUnderTest.getMessageIconStyleClass(simpleMessageInfo);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_MESSAGE_SIGN_QUESTION, messageStyleSimpleInfo);
		final ProductConfigMessageData simpleMessageError = createMessage("Message 4", null, ProductConfigMessageUISeverity.CONFIG);
		final String messageStyleSimpleError = classUnderTest.getMessageIconStyleClass(simpleMessageError);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_MESSAGE_SIGN_INFO, messageStyleSimpleError);
		final ProductConfigMessageData simpleMessageConfig = createMessage("Message 5", null, ProductConfigMessageUISeverity.ERROR);
		final String messageStyleSimpleConfig = classUnderTest.getMessageIconStyleClass(simpleMessageConfig);
		assertEquals(CSSClassResolverImpl.STYLE_CLASS_MESSAGE_SIGN_INFO, messageStyleSimpleConfig);

	}

	@Test
	public void testgetMenuConflictStyleClass() {
		final UiGroupData group = new UiGroupData();
		group.setGroupType(GroupType.CONFLICT);
		group.setCollapsedInSpecificationTree(false);
		final String style = classUnderTest.getMenuConflictStyleClass(group);
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_MENU_CONFLICT_NODE);		
	}

	
	@Test
	public void testgetMenuConflictStyleClassWithConflictHeaderCollapsed() {
		final UiGroupData group = new UiGroupData();
		group.setGroupType(GroupType.CONFLICT_HEADER);
		group.setCollapsedInSpecificationTree(true);
		final String style = classUnderTest.getMenuConflictStyleClass(group);
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_MENU_CONFLICT_HEADER, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_COLLAPSED);		
	}

	@Test
	public void testgetMenuConflictStyleClassWithConflictHeaderExpanded() {
		final UiGroupData group = new UiGroupData();
		group.setGroupType(GroupType.CONFLICT_HEADER);
		group.setCollapsedInSpecificationTree(false);
		final String style = classUnderTest.getMenuConflictStyleClass(group);
		assertContainsStyleClass(style, CSSClassResolverImpl.STYLE_MENU_CONFLICT_HEADER, CSSClassResolverImpl.STYLE_CLASS_MENU_NODE_EXPANDED);		
	}

}
