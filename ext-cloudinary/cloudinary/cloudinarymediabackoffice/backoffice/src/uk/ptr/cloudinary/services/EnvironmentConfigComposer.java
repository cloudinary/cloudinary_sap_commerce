package uk.ptr.cloudinary.services;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class EnvironmentConfigComposer extends GenericForwardComposer<Component> {


    private ConfigurationService configurationService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        String envConfigUrl = getConfigurationService().getConfiguration().getString("cloudinary.config.url");
        String js = String.format("window.envConfig = { baseUrl: '%s' };", envConfigUrl);
        Clients.evalJavaScript(js);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
