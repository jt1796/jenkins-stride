package jt.stride;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class StrideConfiguration extends GlobalConfiguration {

    private String accessToken;
    private String conversationUrl;

    public StrideConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /** @return the singleton instance */
    public static StrideConfiguration get() {
        return GlobalConfiguration.all().get(StrideConfiguration.class);
    }

    public String getAccessToken() {
        return accessToken;
    }

    @DataBoundSetter
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        save();
    }

    public String getConversationUrl() {
        return conversationUrl;
    }

    @DataBoundSetter
    public void setConversationUrl(String conversationUrl) {
        this.conversationUrl = conversationUrl;
        save();
    }
}
