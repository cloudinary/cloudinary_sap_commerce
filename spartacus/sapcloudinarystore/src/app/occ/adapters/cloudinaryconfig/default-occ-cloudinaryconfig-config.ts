//port { ProductScope } from '../../../cloudinaryconfig/model/cloudinaryconfig-scope';
import { OccConfig } from '../../config/occ-config';

export const defaultOccCloudinaryConfigConfig: OccConfig = {
  backend: {
    occ: {
      baseUrl: 'https://localhost:9002/cloudinarymediawebservices/apparel-uk-spa/',
      endpoints: {
        cloudinaryConfig: 'cloudinary/configuration',
      },
    },
  },
};
