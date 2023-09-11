# secret-replacer
Command line application to replace variables with secrets from an ENV file

## Usage

The program will load environmental variables from a `.env` file in the working directory.

By default the program will only replace variables in `.yml`, `.yaml`, `.json` and `.properties` files.

This can be changed by using the `--replacer-env-file-types` flag.

`java -jar secret-replacer.jar [options] <directory>`

Run without arguments to see available options.
