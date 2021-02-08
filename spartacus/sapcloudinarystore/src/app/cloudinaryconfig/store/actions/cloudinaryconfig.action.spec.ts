// import { Product } from '../../../model/product.model';
// import { PRODUCT_DETAIL_ENTITY } from '../product-state';
// import * as fromProduct from './product.action';
// import { EntityScopedLoaderActions } from '../../../state/utils/scoped-loader/entity-scoped-loader.actions';

// describe('Product Actions', () => {
//   describe('LoadProduct Actions', () => {
//     describe('LoadProduct', () => {
//       it('should create an action', () => {
//         const productCode = 'testCode';
//         const action = new fromProduct.LoadProduct(productCode);
//         expect({ ...action }).toEqual({
//           type: fromProduct.LOAD_PRODUCT,
//           payload: productCode,
//           meta: EntityScopedLoaderActions.entityScopedLoadMeta(
//             PRODUCT_DETAIL_ENTITY,
//             productCode,
//             ''
//           ),
//         });
//       });
//     });

//     describe('LoadProductFail', () => {
//       it('should create an action', () => {
//         const payload = { message: 'Load Error' };
//         const productCode = 'productCode';
//         const action = new fromProduct.LoadProductFail(productCode, payload);

//         expect({ ...action }).toEqual({
//           type: fromProduct.LOAD_PRODUCT_FAIL,
//           payload,
//           meta: EntityScopedLoaderActions.entityScopedFailMeta(
//             PRODUCT_DETAIL_ENTITY,
//             productCode,
//             '',
//             payload
//           ),
//         });
//       });
//     });

//     describe('LoadProductSuccess', () => {
//       it('should create an action', () => {
//         const payload: Product = { code: '123' };
//         const action = new fromProduct.LoadProductSuccess(payload);

//         expect({ ...action }).toEqual({
//           type: fromProduct.LOAD_PRODUCT_SUCCESS,
//           payload,
//           meta: EntityScopedLoaderActions.entityScopedSuccessMeta(
//             PRODUCT_DETAIL_ENTITY,
//             payload.code,
//             ''
//           ),
//         });
//       });
//     });
//   });
// });
