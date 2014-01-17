/**
 * Returns true when a given version of a package on given resource match
 * @param resourceId
 * @param expectedVersion
 * @returns {Boolean}
 */
function checkPackageVersionOnResource(resourceId,expectedVersion){
    var installedPackage = ContentManager.getBackingPackageForResource(resourceId);
    if(installedPackage != null){
        common.trace("Found installed version: " +installedPackage.getPackageVersion().getDisplayVersion());
        if(installedPackage.getPackageVersion().getDisplayVersion() == expectedVersion){
            return true;
        }
    }
}