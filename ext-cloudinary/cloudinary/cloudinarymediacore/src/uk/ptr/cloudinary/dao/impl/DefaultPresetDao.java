package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import uk.ptr.cloudinary.dao.PresetDao;
import uk.ptr.cloudinary.model.PresetModel;

public class DefaultPresetDao extends DefaultGenericDao<PresetModel> implements PresetDao {
    public DefaultPresetDao() {
        super(PresetModel._TYPECODE);
    }
}
