package Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ResourceHandler {
    private List<Resource> resources = new ArrayList<>();

    public boolean hasResource(Set<String> taskResources) {
        synchronized (this) {
            for (String taskResource: taskResources) {
                for (var resource: resources) {
                    if (resource.getName().equals(taskResource)) {
                        if (resource.getAmount() <= 0)
                            return false;
                    }
                }
            }
            return true;
        }
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public boolean getResource(Set<String> taskResources) {
        synchronized (this) {
            var indexes = new ArrayList<Integer>();
            for (String taskResource : taskResources) {
                for (int i = 0; i < resources.size(); i++) {
                    if (resources.get(i).getName().equals(taskResource)) {
                        if (resources.get(i).getAmount() <= 0)
                            return false;
                        else
                            indexes.add(i);
                    }
                }
            }

            for (var index : indexes)
                resources.get(index).setAmount(resources.get(index).getAmount() - 1);

            return true;
        }
    }

    public void releaseResource(Set<String> taskResources) {
        synchronized (this) {
            for (String taskResource : taskResources) {
                for (var resource : resources) {
                    if (resource.getName().equals(taskResource)) {
                        resource.setAmount(resource.getAmount() + 1);
                    }
                }
            }
        }
    }

    public String toString() {
        synchronized (this) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Resource resource : resources) {
                stringBuilder.append(resource.getName())
                        .append(": ")
                        .append(resource.getAmount())
                        .append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            return stringBuilder.toString();
        }
    }

    public boolean hasReleasedResource(Set<String> resourcesToBeReleased, Set<String> neededResources) {
        synchronized (this) {
            var availableResources  = new HashSet<String>();
            for (Resource resource: resources)
                if (resource.getAmount() > 0)
                    availableResources.add(resource.getName());

            availableResources.addAll(resourcesToBeReleased);
            return availableResources.containsAll(neededResources);
        }
    }
}
