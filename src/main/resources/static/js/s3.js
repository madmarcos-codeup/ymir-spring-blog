import {S3} from '@aws-sdk/client-s3';
const s3 = new S3({region: 'us-east-2'});

export function foo() {
    console.log("foo called");
}

// Export the handler function
// module.exports = foo;