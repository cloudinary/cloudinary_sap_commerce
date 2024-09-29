/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
(function(){
    //when screenWidth equals imageWidth, the screen displays the right image which has same width with screen's.
    if (window.Imager) {
        Imager.getClosestValue = function getClosestValue(baseValue, candidates){
            if(baseValue > candidates[0] && candidates.length > 1){
                candidates.shift();
                getClosestValue(baseValue,candidates);
            }
            return candidates[0];
        };
    }
})();
