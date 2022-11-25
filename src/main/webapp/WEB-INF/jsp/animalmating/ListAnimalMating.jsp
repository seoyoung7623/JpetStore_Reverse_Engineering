<%@ include file="../common/IncludeTop.jsp"%>
<head>
<%--<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css" rel="stylesheet" />--%>
<link rel="StyleSheet" href="../css/styles.css" type="text/css" media="screen"/>
</head>
<div id="Catalog">
    <div class="container px-4 px-lg-5 mt-5">
        <h2>Animal Mating</h2>
        <hr>
        <br/>
        <div class="row gx-4 gx-lg-5 row-cols-2 row-cols-md-3 row-cols-xl-4 justify-content-center">
            <c:forEach var="mating" items="${actionBean.animalMatingList}">
                <div class="col mb-5">
                    <div class="card h-100" style="box-shadow: 5px 5px 5px 5px gray;">
                        <!-- Product image-->
                        <img id="Immg" style="height:70%;" class="card-img-top" src="${mating.imgUrl}" alt="..." />
                        <!-- Product details-->
                        <div class="card-body p-4">
                            <div class="text-center">
                                <!-- Product name-->
                                <h5 class="fw-bolder">${mating.categoryid}</h5>
                                <!-- Product price-->
                                ${mating.userId}
                            </div>
                        </div>
                        <!-- Product actions-->
                        <div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
                            <div class="text-center">
                                <stripes:link class="btn btn-outline-dark mt-auto" href="#"
                                        beanclass="org.mybatis.jpetstore.web.actions.AnimalActionBean"
                                        event="getMatingInfo">
                                <stripes:param name="id" value="${mating.id}" />
                                INFOMATION
                                </stripes:link>
                                <%--<a class="btn btn-outline-dark mt-auto" href="#">INFOMATION</a>--%>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>

                    <!-- Product actions-->
        </div>
    </div>
    <div style="position: absolute; top:75px; right: 45%;">
        <stripes:link class="Button"
                      beanclass="org.mybatis.jpetstore.web.actions.AnimalActionBean"
                      event="addAnimalMatingView">
            AddAnimal
        </stripes:link>
    </div>
    <div style="position: absolute; top:75px; left: 45%;">
        <stripes:link class="Button"
                      beanclass="org.mybatis.jpetstore.web.actions.AnimalActionBean"
                      event="recommendAnimalMatingView">
            RecommendAnimal
        </stripes:link>
    </div>



</div>


<!-- Bootstrap core JS-->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- Core theme JS-->
<script src="../../js/scripts.js"></script>



<%@ include file="../common/IncludeBottom.jsp"%>