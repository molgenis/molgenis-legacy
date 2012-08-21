package org.molgenis.mutation;

import org.molgenis.core.service.PublicationService;
import org.molgenis.framework.security.Login;
import org.molgenis.mutation.service.CmsService;
import org.molgenis.mutation.service.FastaService;
import org.molgenis.mutation.service.GffService;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.service.StatisticsService;
import org.molgenis.mutation.service.UploadService;
import org.molgenis.pheno.service.PhenoService;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Locates and provides all available application services.
 */
public class ServiceLocator
{
    private ServiceLocator()
    {
        // shouldn't be instantiated
    }

    public static final String BEAN_PREFIX = "";
    private final String DEFAULT_BEAN_REFERENCE_LOCATION = "beanRefFactory.xml";
    private final String DEFAULT_BEAN_REFERENCE_ID = "beanRefFactory";
    private BeanFactoryReference beanFactoryReference;
    private String beanFactoryReferenceLocation;
    private String beanRefFactoryReferenceId;

    private static final ServiceLocator instance = new ServiceLocator();

    /**
     * Gets the shared instance of this Class
     *
     * @return the shared service locator instance.
     */
    public static final ServiceLocator instance()
    {
        return instance;
    }

    /**
     * Initializes the Spring application context from
     * the given <code>beanFactoryReferenceLocation</code>.  If <code>null</code>
     * is specified for the <code>beanFactoryReferenceLocation</code>
     * then the default application context will be used.
     *
     * @param beanFactoryReferenceLocationIn the location of the beanRefFactory reference.
     * @param beanRefFactoryReferenceIdIn the id of the beanRefFactory reference.
     */
    public synchronized void init(final String beanFactoryReferenceLocationIn, final String beanRefFactoryReferenceIdIn)
    {
        this.beanFactoryReferenceLocation = beanFactoryReferenceLocationIn;
        this.beanRefFactoryReferenceId    = beanRefFactoryReferenceIdIn;
        this.beanFactoryReference         = null;
    }

    /**
     * Initializes the Spring application context from
     * the given <code>beanFactoryReferenceLocation</code>.  If <code>null</code>
     * is specified for the <code>beanFactoryReferenceLocation</code>
     * then the default application context will be used.
     *
     * @param beanFactoryReferenceLocationIn the location of the beanRefFactory reference.
     */
    public synchronized void init(final String beanFactoryReferenceLocationIn)
    {
        this.beanFactoryReferenceLocation = beanFactoryReferenceLocationIn;
        this.beanFactoryReference         = null;
    }

    /**
     * Gets the Spring ApplicationContext.
     * @return beanFactoryReference.getFactory()
     */
    public synchronized ApplicationContext getContext()
    {
        if (this.beanFactoryReference == null)
        {
            if (this.beanFactoryReferenceLocation == null)
                this.beanFactoryReferenceLocation = this.DEFAULT_BEAN_REFERENCE_LOCATION;
            if (this.beanRefFactoryReferenceId == null)
                this.beanRefFactoryReferenceId = this.DEFAULT_BEAN_REFERENCE_ID;

            BeanFactoryLocator beanFactoryLocator = ContextSingletonBeanFactoryLocator.getInstance(this.beanFactoryReferenceLocation);
            this.beanFactoryReference = beanFactoryLocator.useBeanFactory(this.beanRefFactoryReferenceId);
        }
        return (ApplicationContext) this.beanFactoryReference.getFactory();
    }

    public synchronized void shutdown()
    {
        ((AbstractApplicationContext) this.getContext()).close();
        if (this.beanFactoryReference != null)
        {
            this.beanFactoryReference.release();
            this.beanFactoryReference = null;
        }
    }

    /**
     * Gets an instance of {@link SecurityService}.
     * @return SecurityService from getContext().getBean("securityService")
     */
    public final Login getSecurityService()
    {
        return (Login)
            getContext().getBean("securityService");
    }

    /**
     * Gets an instance of {@link StatisticsService}.
     * @return StatisticsService from getContext().getBean("statisticsService")
     */
    public final StatisticsService getStatisticsService()
    {
        return (StatisticsService)
            getContext().getBean("statisticsService");
    }

    /**
     * Gets an instance of {@link UploadService}.
     * @return UploadService from getContext().getBean("uploadService")
     */
    public final UploadService getUploadService()
    {
        return (UploadService)
            getContext().getBean("uploadService");
    }

    /**
     * Gets an instance of {@link SearchService}.
     * @return SearchService from getContext().getBean("searchService")
     */
    public final SearchService getSearchService()
    {
        return (SearchService)
            getContext().getBean("searchService");
    }
    
    /**
     * Gets an instance of {@link FastaService}.
     * @return FastaService from getContext().getBean("fastaService")
     */
    public final FastaService getFastService()
    {
        return (FastaService)
            getContext().getBean("fastaService");
    }
    
    /**
     * Gets an instance of {@link GffService}.
     * @return GffService from getContext().getBean("gffService")
     */
    public final GffService getGffService()
    {
        return (GffService)
            getContext().getBean("gffService");
    }

    /**
     * Gets an instance of {@link PhenoService}.
     * @return PhenoService from getContext().getBean("phenoService")
     */
    public final PhenoService getPhenoService()
    {
        return (PhenoService)
            getContext().getBean("phenoService");
    }

    /**
     * Gets an instance of {@link PublicationService}.
     * @return PublicationService from getContext().getBean("publicationService")
     */
    public final PublicationService getPublicationService()
    {
        return (PublicationService)
            getContext().getBean("publicationService");
    }

    /**
     * Gets an instance of {@link CmsService}.
     * @return CmsService from getContext().getBean("cmsService")
     */
    public final CmsService getCmsService()
    {
        return (CmsService)
            getContext().getBean("cmsService");
    }

    /**
     * Gets an instance of the given service.
     * @param serviceName
     * @return getContext().getBean(BEAN_PREFIX + serviceName)
     */
    public final Object getService(String serviceName)
    {
        return getContext().getBean(BEAN_PREFIX + serviceName);
    }
}
