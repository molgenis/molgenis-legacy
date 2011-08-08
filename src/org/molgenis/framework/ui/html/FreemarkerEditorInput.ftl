<#--div style="border: 1px #AAA solid;"> 
<textarea id="${name}" name="${name}" cols="80" rows="5"> 
<#if value?exists>${value}</#if>
</textarea>
</div-->
									
<textarea id="${name}" name="${name}" cols="80" rows="5"><#if value?exists>${value}</#if></textarea>
 
<script type="text/javascript"> 
	var ${name}_editor = CodeMirror.fromTextArea(document.getElementById('${name}'), {
	width: "551px", 
	height: "139px",
	onChange: save,
    textWrapping: false,
    iframeClass: "CodeMirror-iframe",
    parserfile: "../contrib/freemarker/js/parsefreemarker.js",
    stylesheet: "generated-res/lib/codemirror-1.0/contrib/freemarker/css/freemarkercolors.css",
    path: "generated-res/lib/codemirror-1.0/js/",
    continuousScanning: 500,
    autoMatchParens: true,
    lineNumbers: true,
    markParen: function(node, ok) { 
        node.style.backgroundColor = ok ? "#CCF" : "#FCC#";
        if(!ok) {
            node.style.color = "red";
        }
    },
    unmarkParen: function(node) { 
         node.style.backgroundColor = "";
         node.style.color = "";
    },
    indentUnit: 4
  });
  
$(".CodeMirror-wrapping").resizable();
</script>
