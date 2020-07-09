# Grafiki

## Usage

### Installation (as an admin)

#### Metabase

 - Go to Metabase admin, click on 'Embedding in other applications' menu
 - Enable embedding
 - Copy the embedding secret ley (will be used below)

#### Confluence

 - Install the plug-in in Confluence server
 - Go to Confluence admin, Grafiki section (in Configuration menu)
 - In the 'Metabase base URL field', enter the base URL of the Metabase instance from which you want to display charts, **without a trailing slash**. E.g.: https://www.example.com
 - In the Metabase secret key field, enter the secret key you create earlier
 - Save

### Usage
#### Metabase

You need to enable public embedding for each question that you want to embed in Confluence:

 - Go to the question
 - Click on the Share button (an upward arrow from a box)
 - Click on 'embed this question in an application'
 - If the question is a native SQL query, in the right section you can configure what to do with query parameters (prevent using them, force a specific value, let the viewer set a value)
 - Click on Publish

#### Confluence

 - Edit a page and put the cursor where you would like to insert a chart
 - Click on 'Insert macro' (a + button), then on 'Other macros'
 - Search for 'metabase' and click on the macro 'Metabase chart'
 - In the field question, paste the URL of the chart that you want to insert in the page. It should be a question, not a dashboard or anything else.
 - Click on Insert
 - Save the page

## Development

Some useful SDK commands:

* atlas-run   -- installs this plugin into a local Confluence deployment and starts it on localhost
* atlas-package -- Rebuild this plugin and hotswap it on the running server
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK
