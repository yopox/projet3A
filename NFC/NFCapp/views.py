from django.http import HttpResponse
from django.template import loader
import nxppy
import time


def index(request):
    global uid
    template = loader.get_template('NFCapp/index.html')
    mifare = nxppy.Mifare()

    # Print card UIDs as they are detected
    while True:
        try:
            uid = mifare.select()
            text = mifare.read()
            break
        except nxppy.SelectError:
            # SelectError is raised if no carxd is in the field.
            pass
        except MemoryError:
            text = "Error reading text"
            break

        time.sleep(1)

    context = {
        'uid': uid,
        'text': text
    }
    return HttpResponse(template.render(context, request))
