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
import org.kohsuke.stapler.DataBoundConstructor;

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
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        listener.getLogger().print("I am running!");
        return true;
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
