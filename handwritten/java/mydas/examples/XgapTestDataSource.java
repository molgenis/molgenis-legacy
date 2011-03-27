package mydas.examples;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;

import uk.ac.ebi.mydas.configuration.DataSourceConfiguration;
import uk.ac.ebi.mydas.controller.CacheManager;
import uk.ac.ebi.mydas.datasource.RangeHandlingAnnotationDataSource;
import uk.ac.ebi.mydas.exceptions.BadReferenceObjectException;
import uk.ac.ebi.mydas.exceptions.CoordinateErrorException;
import uk.ac.ebi.mydas.exceptions.DataSourceException;
import uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException;
import uk.ac.ebi.mydas.extendedmodel.DasUnknownFeatureSegment;
import uk.ac.ebi.mydas.model.DasAnnotatedSegment;
import uk.ac.ebi.mydas.model.DasComponentFeature;
import uk.ac.ebi.mydas.model.DasFeature;
import uk.ac.ebi.mydas.model.DasType;

public class XgapTestDataSource implements RangeHandlingAnnotationDataSource {
	CacheManager cacheManager = null;
	ServletContext svCon;
	Map<String, String> globalParameters;
	DataSourceConfiguration config;
	XgapTestManager xgap;

	public void init(ServletContext servletContext,
			Map<String, String> globalParameters,
			DataSourceConfiguration dataSourceConfig)
			throws DataSourceException {
		this.svCon = servletContext;
		this.globalParameters = globalParameters;
		this.config = dataSourceConfig;
		xgap = new XgapTestManager();
	}

	public void destroy() {
		xgap.close();
	}

	public DasAnnotatedSegment getFeatures(String segmentId, int start,
			int stop, Integer maxbins) throws BadReferenceObjectException,
			CoordinateErrorException, DataSourceException {
		return xgap.getSubmodelBySegmentId(segmentId, start, stop);
	}

	public DasAnnotatedSegment getFeatures(String segmentId, Integer maxbins)
			throws BadReferenceObjectException, DataSourceException {
		return xgap.getSubmodelBySegmentId(segmentId, -1, -1);
	}

	public Collection<DasAnnotatedSegment> getFeatures(
			Collection<String> featureIdCollection, Integer maxbins)
			throws UnimplementedFeatureException, DataSourceException {
		Collection<DasAnnotatedSegment> segments = xgap
				.getSubmodelByFeatureId(featureIdCollection);
		Collection<DasAnnotatedSegment> segmentsResponse = new ArrayList<DasAnnotatedSegment>();
		for (String featureId : featureIdCollection) {
			boolean found = false;
			for (DasAnnotatedSegment segment : segments) {
				for (DasFeature feature : segment.getFeatures())
					if (feature.getFeatureId().equals(featureId)) {
						segmentsResponse.add(new DasAnnotatedSegment(segment
								.getSegmentId(), segment.getStartCoordinate(),
								segment.getStopCoordinate(), segment
										.getVersion(), segment
										.getSegmentLabel(), Collections
										.singleton(feature)));
						found = true;
						break;
					} else if (this.lookInside((DasComponentFeature) feature,
							featureId, segmentsResponse, segment)) {
						found = true;
						break;
					}
			}
			if (!found)
				segmentsResponse.add(new DasUnknownFeatureSegment(featureId));
		}
		return segmentsResponse;
	}

	private boolean lookInside(DasComponentFeature component, String featureId,
			Collection<DasAnnotatedSegment> segmentsResponse,
			DasAnnotatedSegment segment) throws DataSourceException {
		if (component.hasSubParts()) {
			for (DasComponentFeature subcomponent : component
					.getReportableSubComponents()) {
				if (subcomponent.getFeatureId().equals(featureId)) {
					segmentsResponse.add(new DasAnnotatedSegment(segment
							.getSegmentId(), segment.getStartCoordinate(),
							segment.getStopCoordinate(), segment.getVersion(),
							segment.getSegmentLabel(), Collections
									.singleton((DasFeature) subcomponent)));
					return true;
				} else if (this.lookInside(subcomponent, featureId,
						segmentsResponse, segment))
					return true;
			}
		}
		return false;

	}

	public Collection<DasType> getTypes() throws DataSourceException {
		return xgap.getTypes();
	}

	public URL getLinkURL(String field, String id)
			throws UnimplementedFeatureException, DataSourceException {
		throw new UnimplementedFeatureException("No implemented");
	}

	public Integer getTotalCountForType(DasType type)
			throws DataSourceException {
		return xgap.getTotalCountForType(type.getId());
	}

	public void registerCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

}