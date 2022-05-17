from flask import * 
import os,io,sys

app = Flask(__name__)
@app.route("/update")
def download():
    return send_file(sys.argv[1],as_attachment=True)


@app.route("/")
def main():
    return jsonify("Main PalOS web server for software updates - devteam at cyfly")
if __name__ == '__main__':
    app.run(port=26197,debug=True)
