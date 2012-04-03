// dataaccessmanager    

/**
 * @author mfoley@redhat.com (Michael Foley)
 * March 30, 2012
 */

var query =
            "SELECT r " +
            "FROM Resource r " +
            "WHERE ( r.inventoryStatus = org.rhq.core.domain.resource.InventoryStatus.COMMITTED " +
            "AND LOWER( r.resourceType.name ) like 'service-alpha' " +
            "AND LOWER( r.parentResource.name ) like 'server-omega-0')";

// test execute query

    var resources = DataAccessManager.executeQuery(query);

    Assert.assertNumberEqualsJS(resources.size(), 0, "Expected to get back 0 resources");

// test execute query with paging 

    var pageControl = PageControl();
    pageControl.pageNumber = 0;
    pageControl.pageSize = 5;
    pageControl.setPrimarySort('name', PageOrdering.ASC);

    var resources = DataAccessManager.executeQueryWithPageControl(query, pageControl);

    Assert.assertNumberEqualsJS(resources.size(), 0, "Failed to fetch first page of resources");

/*  To Do -- modify query to query more stuff

    Assert.assertEquals(resources.get(0).name, 'service-alpha-0', 'Failed to sort first page in ascending order');
    Assert.assertEquals(resources.get(4).name, 'service-alpha-4', 'Failed to sort first page in ascending order');

    pageControl.pageNumber = 1;
    resources = DataAccessManager.executeQueryWithPageControl(query, pageControl);

    Assert.assertNumberEqualsJS(resources.size(), 0, "Failed to fetch second page of resources");
    Assert.assertEquals(resources.get(0).name, 'service-alpha-5', 'Failed to sort second page in ascending order');
    Assert.assertEquals(resources.get(4).name, 'service-alpha-9', 'Failed to sort second page in ascending order');
*/
