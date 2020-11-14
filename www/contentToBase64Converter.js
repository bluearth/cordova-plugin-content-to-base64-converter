module.exports = {
    import: function (uri, success, failure) {
        cordova.exec(success, failure, "ContentToBase64Converter", "convert", [ filter ]);
    }
};
