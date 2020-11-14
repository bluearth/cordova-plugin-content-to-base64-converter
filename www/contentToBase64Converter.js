module.exports = {
    convert: function (filter, success, failure) {
        cordova.exec(success, failure, "ContentToBase64Converter", "convert", [ filter ]);
    }
};
