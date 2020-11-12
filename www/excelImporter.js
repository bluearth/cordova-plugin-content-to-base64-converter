module.exports = {
    import: function (uri, success, failure) {
        cordova.exec(success, failure, "ExcelImporter", "import", [ uri ]);
    }
};
