<!-- menu -->


<!-- page -->
<h1>${screen.page.title}</h1>
${screen.page.content}
Tags: ${screen.page.pageType}

<!-- site map for this group -->
<div class="row">
    
    <div class="span2">
    <h4>About</h4>
	<ul><#list screen.about as item>
	<li><a href="">${item.title}</a></li>
	</#list></ul>
    </div>

    <div class="span2">
    <h4>Projects</h4>
    <ul><#list screen.projects as item>
	<li><a href="">${item.title}</a></li>
	</#list></ul>
    </div>
    
    <div class="span2">
    <h4>Output</h4>
    <ul><#list screen.outputs as item>
	<li><a href="">${item.title}</a></li>
	</#list></ul>
    </div>
    
    <div class="span2">
    <h4>News</h4>
    <ul><#list screen.news as item>
	<li><a href="">${item.title}</a></li>
	</#list></ul>
    </div>
    
    <div class="span4"></div>
</div>

<!--run at end -->
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>