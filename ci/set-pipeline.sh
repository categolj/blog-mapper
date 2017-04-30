#!/bin/sh
echo y | fly -t home sp -p blog-blog-mapper -c pipeline.yml -l ../../credentials.yml
