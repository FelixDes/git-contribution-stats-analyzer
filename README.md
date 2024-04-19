# git-usage-stats

Simple CLI tool for collecting metrics from git repository

## User guide

#### Installing

1. Download distribution archive from the [release page](https://github.com/FelixDes/git-usage-stats/releases)
2. Extract it
3. Run script from `bin` folder for your OS

#### Usage

```
Usage: GitUsageStats options_list  
Subcommands:  
    gmfp - Calculate the pair of developers who contribute together in the biggest number of files
    fmfp - Calculate the pairs of developers per files/folders who most frequently contribute to the same files/folders

Options:
    --help, -h -> Usage info
```

## Available metrics

### FMFp

Pairs of developers who most frequently contribute to the same files/folders in a git repository.

### GMFp

Pair of developers who contribute together in the biggest number of files in a git repository.

## Contributing

If you want to improve the project, follow the steps below:

* Firstly, check the `issues` page - maybe your enchantment is already under consideration
* If there are no corresponding issues - create a new one
* Fork the repository
* Create your branch from `master`
* Create pull request to `master` branch