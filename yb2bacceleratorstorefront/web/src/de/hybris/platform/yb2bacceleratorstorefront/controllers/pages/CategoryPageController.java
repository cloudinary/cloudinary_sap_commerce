/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.controllers.pages;


import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCategoryPageController;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for a category page
 */
@Controller
// FRAMEWORK_UPDATE - TODO - AntPathMatcher was replaced with PathPatternParser as the new default path parser in Spring 6. Adjust this path to the new matching rules or re-enable deprecated AntPathMatcher. Consult "Adapting to PathPatternParser new default URL Matcher" JDK21 Upgrade Step in SAP Help documentation.
@RequestMapping(value = "/**/c")
public class CategoryPageController extends AbstractCategoryPageController {

    @GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN)
    public String category(@PathVariable("categoryCode") final String categoryCode, 
                           @RequestParam(value = "q", required = false) final String searchQuery,
                           @RequestParam(value = "page", defaultValue = "0") final int page,
                           @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                           @RequestParam(value = "sort", required = false) final String sortCode, final Model model,
                           final HttpServletRequest request, final HttpServletResponse response) throws UnsupportedEncodingException {
        return performSearchAndGetResultsPage(categoryCode, searchQuery, page, showMode, sortCode, model, request, response);
    }

    @ResponseBody
    @GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/facets")
    public FacetRefinement<SearchStateData> getFacets(@PathVariable("categoryCode") final String categoryCode,
                                                      @RequestParam(value = "q", required = false) final String searchQuery,
                                                      @RequestParam(value = "page", defaultValue = "0") final int page,
                                                      @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                                      @RequestParam(value = "sort", required = false) final String sortCode) throws UnsupportedEncodingException {
        return performSearchAndGetFacets(categoryCode, searchQuery, page, showMode, sortCode);
    }

    @ResponseBody
    @GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/results")
    public SearchResultsData<ProductData> getResults(@PathVariable("categoryCode") final String categoryCode,
                                                     @RequestParam(value = "q", required = false) final String searchQuery,
                                                     @RequestParam(value = "page", defaultValue = "0") final int page,
                                                     @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                                     @RequestParam(value = "sort", required = false) final String sortCode) throws UnsupportedEncodingException {
        return performSearchAndGetResultsData(categoryCode, searchQuery, page, showMode, sortCode);
    }
}
