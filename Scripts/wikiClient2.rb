require 'rubygems'
require 'media_wiki'
require 'nokogiri'
require 'open-uri'
#require 'mysql'
require 'dbi'


#htmldoc = Nokogiri::HTML(mw.render('Apple_Inc.'))
#puts htmldoc.xpath("//p").first


#begin
# db = Mysql.new('qadatabase.cysieoc6uabv.us-east-1.rds.amazonaws.com', 'reuben', 'yellow13', 'sportaneousqa')
#rescue Mysql::Error
# puts "Oh noes! We could not connect to our database. -_-;;"
#  exit 1
#end


def sqlQuery(name,dbh)
	mw = MediaWiki::Gateway.new('http://en.wikipedia.org/w/api.php')
	htmldoc = Nokogiri::HTML(mw.render(name))
	description = htmldoc.xpath("//p").first
   dbh.do("UPDATE `sportaneousqa`.`facilities`
	SET
	`long_description` = ?
	WHERE `name` = ?",
          description, name)
end




def test
	begin
	results = db.query "SELECT * FROM facilities"
	  puts "Number of users #{results.num_rows}"
	  results.each_hash do |row|
	    puts row["name"]
	  end
	  results.free
	ensure
	  db.close
	end
end


 begin
     # connect to the MySQL server
     dbh = DBI.connect("DBI:Mysql:sportaneousqa:qadatabase.cysieoc6uabv.us-east-1.rds.amazonaws.com", "reuben", "yellow13")
     # get server version string and display it
     row = dbh.select_one("SELECT VERSION()")
     puts "Server version: " + row[0]
    sqlQuery("Central Park",dbh)
   rescue DBI::DatabaseError => e
     puts "An error occurred"
     puts "Error code: #{e.err}"
     puts "Error message: #{e.errstr}"
   ensure
     # disconnect from server
     dbh.disconnect if dbh
   end
 


