/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { dirname } from 'node:path';
import { fileURLToPath } from 'node:url';
import defaultEslintConfig from 'smartedit-build/config/eslint.config.mjs';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export default [
    ...defaultEslintConfig,
    {
        languageOptions: {
            parserOptions: {
                projectService: {
                    allowDefaultProject: ['eslint.config.mjs']
                },
                tsconfigRootDir: __dirname
            }
        }
    }
];
