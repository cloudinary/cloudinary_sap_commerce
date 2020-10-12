/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
module.exports = (function() {
    /***
     *  Naming:
     *  File or Files masks should end in File or Files,
     *  ex: someRoot.path.myBlaFiles = /root/../*.*
     *
     *  General rules:
     *  No copy paste
     *  No duplicates
     *  Avoid specific files when possible, try to specify folders
     *  What happens to cloudinarymediasmartedit, happens to cloudinarymediasmarteditContainer
     *  Try to avoid special cases and exceptions
     */
    var lodash = require('lodash');

    var paths = {};

    paths.tests = {};

    paths.tests.root = 'jsTests';
    paths.tests.testsRoot = paths.tests.root + '/tests';
    paths.tests.cloudinarymediasmarteditContainerTestsRoot =
        paths.tests.testsRoot + '/cloudinarymediasmarteditContainer';
    paths.tests.cloudinarymediasmarteditContainere2eTestsRoot =
        paths.tests.cloudinarymediasmarteditContainerTestsRoot + '/e2e';

    paths.tests.cloudinarymediasmartedite2eTestFiles = paths.tests.root + '/e2e/**/*Test.js';
    paths.tests.cloudinarymediasmarteditContainere2eTestFiles =
        paths.tests.cloudinarymediasmarteditContainere2eTestsRoot + '/**/*Test.js';

    paths.e2eFiles = [
        paths.tests.cloudinarymediasmartedite2eTestFiles,
        paths.tests.cloudinarymediasmarteditContainere2eTestFiles
    ];

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        smarteditDirName: 'smartedit',
        smarteditcontainerDirName: 'smarteditcontainer',
        smarteditcommonsDirName: 'smarteditcommons'
    };

    return paths;
})();
