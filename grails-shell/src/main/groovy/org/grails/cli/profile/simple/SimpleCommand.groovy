package org.grails.cli.profile.simple

import org.grails.cli.profile.CommandDescription
import org.grails.cli.profile.ExecutionContext

class SimpleCommand {
    String name
    File file
    Map<String, Object> data
    SimpleProfile profile
    private List<SimpleCommandStep> steps
    
    CommandDescription getDescription() {
        new CommandDescription(name: name, description: data?.description, usage: data?.usage)
    }

    List<SimpleCommandStep> getSteps() {
        if(steps==null) {
            steps = []
            data.steps.each { 
                Map<String, String> stepParameters = it.collectEntries { k,v -> [k as String, v as String] }
                switch(stepParameters.command) {
                    case 'render':
                        steps.add(new RenderCommandStep(commandParameters: stepParameters, command: this))
                        break
                }
            }
        }
        steps
    }
        
    public boolean handleCommand(ExecutionContext context) {
        if(!context.commandLine.getRemainingArgs()) {
            context.console.error("Expecting an argument to $name.")
            context.console.info("${description.usage}")
            return true
        }
        for(SimpleCommandStep step : getSteps()) {
            if(!step.handleStep(context)) {
                break
            }
        }
        true
    }
}