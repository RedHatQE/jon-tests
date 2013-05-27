
var predicates = metricsTemplates.predicates;
platformTypes = resourceTypes.find({plugin:"Platforms"});

metricsTemplates.disable(platformTypes, predicates.isCallTime);
metricsTemplates.setCollectionInterval(platformTypes,90, predicates.isNumeric);
metricsTemplates.disable(platformTypes);
metricsTemplates.enable(platformTypes);
