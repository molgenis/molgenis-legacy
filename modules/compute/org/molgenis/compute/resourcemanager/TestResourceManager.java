package org.molgenis.compute.resourcemanager;

public class TestResourceManager
{
    public static void main(String[] args)
    {
        ResourceManager man = new ResourceManager();
        man.setSettings(3000, 2000);
        man.start();
    }
}
