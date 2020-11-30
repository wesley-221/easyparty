package com.easyparty;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ws.PartyService;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = "EasyParty",
        description = "Create a party without having to add people as a friend on Discord."
)
@Slf4j
public class EasyPartyPlugin extends Plugin {
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private PartyService partyService;

    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon.png");
        EasyPartyPanel easyPartyPanel = new EasyPartyPanel(partyService);

        NavigationButton navigationButton = NavigationButton.builder()
                .priority(9)
                .tooltip("EasyParty")
                .icon(icon)
                .panel(easyPartyPanel)
                .build();

        clientToolbar.addNavigation(navigationButton);
    }
}
