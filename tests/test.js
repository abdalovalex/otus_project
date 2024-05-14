var newman = require('newman'),
    async = require('async');

const PARALLEL_RUN_COUNT = 5

parallelCollectionRun = function (done) {
    newman.run({
        collection: require('./tests.json'),
        reporters: 'cli'
    }, function (err, summary) {
        if (err) {
            throw err;
        }
        summary.run.executions.forEach(exec => {
            console.log('Request name:', exec.item.name);
            try {
                console.log('Response:', JSON.parse(exec.response.stream));
            } catch (error) {
                console.log('Response:', exec.response.body);
            }

        });
    });
};

let commands = []
for (let index = 0; index < PARALLEL_RUN_COUNT; index++) {
    commands.push(parallelCollectionRun);
}

async.parallel(
    commands,
    (err, results) => {
        err && console.error(err);

        results.forEach(function (result) {
            var failures = result.run.failures;
            console.info(failures.length ? JSON.stringify(failures.failures, null, 2) :
                `${result.collection.name} run successfully.`);
        });
    });