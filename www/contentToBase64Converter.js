module.exports = {
    convert: function (uri, success, failure) {
        cordova.exec(success, failure, "ContentToBase64Converter", "convert", [ filter ]);
    }
};
