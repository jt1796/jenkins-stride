package jt.stride;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class StridePublisher extends Notifier {

    @Extension
    public static final StrideDescriptor DESCRIPTOR = new StrideDescriptor();

    private final boolean sendOnStart;
    private final String sendMsg;

    @DataBoundConstructor
    public StridePublisher(boolean sendOnStart, String sendMsg) {
        this.sendOnStart = sendOnStart;
        this.sendMsg = sendMsg;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        //TODO: parameterize the conversation id in the build.
        //TODO: URL should be configured in global settings.
        listener.getLogger().println("Sending to Stride");
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("https://api.atlassian.com/site/f96cf489-a553-4645-af11-cb766cb85c23/conversation/b3b26d80-3f72-4aec-bdf1-68976238c74f/message");
        String body = buildMessage(build);
        post.setRequestBody(body);
        post.addRequestHeader(new Header("Content-Type", "application/json"));
        post.addRequestHeader(new Header("Authorization", "Bearer " + StrideConfiguration.get().getAccessToken()));
        try {
            int statusCode = client.executeMethod(post);
            if (statusCode >= 400) {
                listener.getLogger().println("Error sending message to stride, received a " + statusCode + " status code.");
                return false;
            }
        } catch(IOException e) {
            listener.getLogger().println(e.getMessage());
            return false;
        }
        return true;
    }

    private String buildMessage(AbstractBuild build) {
        //TODO: Json could be made invalid, sanitize this.sendMsg
        //TODO: This isn't really the build status just something good enough
        String msg = this.sendMsg;
        msg = msg.replaceAll("\\$BUILD_NUMBER", "" + build.number);
        msg = msg.replaceAll("\\$BUILD_NAME", build.getParent().getDisplayName());
        msg = msg.replaceAll("\\$BUILD_STATUS", build.getBuildStatusSummary().message);
        msg = "{\"body\":{\"version\":1,\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\""
                + msg
                + "\"}]}]}}";
        return msg;
    }

    public boolean isSendOnStart() {
        return sendOnStart;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    private static class StrideDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Stride";
        }
    }
}
