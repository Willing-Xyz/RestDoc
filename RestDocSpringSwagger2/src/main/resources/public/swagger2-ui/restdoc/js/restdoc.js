
function configUI(baseUrl) {
    $.ajax({
        url: baseUrl + "/swagger2/swaggerUIConfiguration",
        dataType: "json",
        success: function( result ) {
           window.ui = SwaggerUIBundle({
               url: "../swagger2.json",
               dom_id: '#swagger-ui',
               deepLinking: result.deepLinking,
               displayOperationId: result.displayOperationId,

               docExpansion: result.docExpansion,
               defaultModelsExpandDepth: result.defaultModelsExpandDepth,
               defaultModelExpandDepth: result.defaultModelExpandDepth,
               maxDisplayedTags: result.maxDisplayedTags,
               showExtensions: result.showExtensions,
               showCommonExtensions: result.showCommonExtensions,
               defaultModelRendering: result.defaultModelRendering,
               displayRequestDuration: result.displayRequestDuration,

               presets: [
                   SwaggerUIBundle.presets.apis,
                   SwaggerUIStandalonePreset
               ],
               plugins: [
                   SwaggerUIBundle.plugins.DownloadUrl
               ],
               layout: result.layout,
               onComplete: function () {
                   handleAuthorizeAction();
               }
           })
        }
    });
}

function getBaseUrl() {
    var urlMatches = /(.*)\/swagger2-ui\/index.html.*/.exec(window.location.href);
    return urlMatches[1];
}

console.log(getBaseUrl());

window.onload = function () {
    configUI(getBaseUrl());
}




