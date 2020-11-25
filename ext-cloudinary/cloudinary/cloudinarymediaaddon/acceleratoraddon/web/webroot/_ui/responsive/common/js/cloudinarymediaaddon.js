/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

$(document).ready(function(){
    $('body').append($('<script src="https://product-gallery.cloudinary.com/all.js"></script>'));
});


  const myGallery= cloudinary.galleryWidget({
    container: "#my-gallery",
    cloudName: "demo",
    mediaAssets: [
      {tag: "electric_car_product_gallery_demo"}, // by default mediaType: "image"
      {tag: "electric_car_product_gallery_demo", mediaType: "video"},
      {tag: "electric_car_360_product_gallery_demo", mediaType: "spin"}
    ],
    carouselLocation: 'bottom'
  });

  myGallery.render();
