# -*- coding: utf-8 -*-

import sys, os
from recommonmark.parser import CommonMarkParser

project = u'sphinx-gradle-plugin'
copyright = u'2017, Trustin Lee et al'
version = '1.0'
release = '1.0.6'

# General options
needs_sphinx = '1.0'
master_doc = 'index'
pygments_style = 'tango'
add_function_parentheses = True

extensions = ['sphinx.ext.autodoc', 'javasphinx',
              'sphinxcontrib.inlinesyntaxhighlight', 'sphinxcontrib.plantuml']
templates_path = ['_templates']
exclude_trees = ['.build']
source_suffix = ['.rst', '.md']
source_encoding = 'utf-8-sig'
source_parsers = {
  '.md': CommonMarkParser
}

# HTML options
html_theme = 'sphinx_rtd_theme'
html_short_title = "sphinx-gradle-plugin"
htmlhelp_basename = 'sphinx-gradle-plugin-doc'
html_use_index = True
html_show_sourcelink = False
html_static_path = ['_static']

# PlantUML options
plantuml = os.getenv('plantuml')
